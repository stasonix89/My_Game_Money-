// src/pages/budget/BudgetIncomePage.tsx
import React, { useEffect, useMemo, useState } from "react";
import "./budget-income.css";
import {
    budgetIncomeApi,
    type BudgetIncomeRequest,
    type BudgetIncomeResponse,
    type ExtraIncomeDto,
} from "../../api/budgetIncomeApi";
import {
    Line,
    LineChart,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer,
    PieChart,
    Pie,
    Cell,
    Legend,
} from "recharts";

const MONTHS_RU = [
    "Январь",
    "Февраль",
    "Март",
    "Апрель",
    "Май",
    "Июнь",
    "Июль",
    "Август",
    "Сентябрь",
    "Октябрь",
    "Ноябрь",
    "Декабрь",
];

const PIE_COLORS = ["#f6d365", "#f6e27a", "#ffc857", "#e0aaff"];

type ExtraIncomeRow = ExtraIncomeDto & { id: string };

const createEmptyExtraIncome = (): ExtraIncomeRow => ({
    id: Math.random().toString(36).slice(2),
    label: "",
    amount: 0,
});

const getCurrentYearMonth = () => {
    const now = new Date();
    return {
        year: now.getFullYear(),
        month: now.getMonth() + 1,
    };
};

const BudgetIncomePage: React.FC = () => {
    const { year: currentYear, month: currentMonth } = getCurrentYearMonth();

    const [year, setYear] = useState<number>(currentYear);
    const [month, setMonth] = useState<number>(currentMonth);

    const [yearlyDividends, setYearlyDividends] = useState<number>(0);
    const [depositAmount, setDepositAmount] = useState<number>(0);
    const [depositRateYearly, setDepositRateYearly] = useState<number>(0);
    const [salaryMonthly, setSalaryMonthly] = useState<number>(0);
    const [extraIncomes, setExtraIncomes] = useState<ExtraIncomeRow[]>([
        createEmptyExtraIncome(),
    ]);

    const [serverResult, setServerResult] = useState<BudgetIncomeResponse | null>(
        null
    );

    const [isLoading, setIsLoading] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // --- Загрузка данных за выбранный месяц ---
    useEffect(() => {
        const loadData = async () => {
            setIsLoading(true);
            setError(null);
            try {
                const data = await budgetIncomeApi.getIncome(year, month);
                setServerResult(data);

                setYearlyDividends(data.yearlyDividends ?? 0);
                setDepositAmount(data.depositAmount ?? 0);
                setDepositRateYearly(data.depositRateYearly ?? 0);
                setSalaryMonthly(data.salaryMonthly ?? 0);

                if (data.extraIncomes && data.extraIncomes.length > 0) {
                    setExtraIncomes(
                        data.extraIncomes.map((item) => ({
                            ...item,
                            id: Math.random().toString(36).slice(2),
                        }))
                    );
                } else {
                    setExtraIncomes([createEmptyExtraIncome()]);
                }
            } catch {
                // если нет данных за месяц — просто обнуляем форму
                setServerResult(null);
                setYearlyDividends(0);
                setDepositAmount(0);
                setDepositRateYearly(0);
                setSalaryMonthly(0);
                setExtraIncomes([createEmptyExtraIncome()]);
            } finally {
                setIsLoading(false);
            }
        };

        void loadData();
    }, [year, month]);

    // --- Локальный перерасчёт итогов (для превью до сохранения) ---
    const localComputed = useMemo(() => {
        const dividendsMonthly = yearlyDividends / 12;
        const depositInterestMonthly =
            (depositAmount * (depositRateYearly / 100)) / 12;
        const extraSum = extraIncomes.reduce(
            (sum, item) => sum + (Number.isNaN(item.amount) ? 0 : item.amount),
            0
        );
        const totalMonthlyIncome =
            dividendsMonthly + depositInterestMonthly + salaryMonthly + extraSum;

        return {
            dividendsMonthly,
            depositInterestMonthly,
            extraSum,
            totalMonthlyIncome,
        };
    }, [
        yearlyDividends,
        depositAmount,
        depositRateYearly,
        salaryMonthly,
        extraIncomes,
    ]);

    // --- Итоговые значения, которые показываем пользователю ---
    // Если backend уже посчитал текущий месяц → берём его.
    // Если нет (новый месяц до сохранения) → используем локальный расчёт.
    const isServerForCurrentMonth =
        serverResult &&
        serverResult.year === year &&
        serverResult.month === month;

    const effectiveDividendsMonthly = isServerForCurrentMonth
        ? serverResult!.dividendsMonthly
        : localComputed.dividendsMonthly;

    const effectiveDepositInterestMonthly = isServerForCurrentMonth
        ? serverResult!.depositInterestMonthly
        : localComputed.depositInterestMonthly;

    const effectiveExtraMonthly = isServerForCurrentMonth
        ? serverResult!.extraIncomesMonthly
        : localComputed.extraSum;

    const effectiveTotalMonthlyIncome = isServerForCurrentMonth
        ? serverResult!.totalMonthlyIncome
        : localComputed.totalMonthlyIncome;

    // --- payload для сохранения ---
    const buildRequestPayload = (): BudgetIncomeRequest => ({
        year,
        month,
        yearlyDividends,
        depositAmount,
        depositRateYearly,
        salaryMonthly,
        extraIncomes: extraIncomes
            .filter((row) => row.label.trim() !== "" && !Number.isNaN(row.amount))
            .map<ExtraIncomeDto>((row) => ({
                label: row.label.trim(),
                amount: row.amount,
            })),
    });

    // --- сохранить месяц ---
    const handleSave = async () => {
        setIsSaving(true);
        setError(null);
        try {
            const payload = buildRequestPayload();
            const saved = await budgetIncomeApi.saveIncome(payload);
            setServerResult(saved);

            // Синхронизируем форму и доп. доходы с backend,
            // чтобы после сохранения всё оставалось на месте.
            setYearlyDividends(saved.yearlyDividends ?? 0);
            setDepositAmount(saved.depositAmount ?? 0);
            setDepositRateYearly(saved.depositRateYearly ?? 0);
            setSalaryMonthly(saved.salaryMonthly ?? 0);

            if (saved.extraIncomes && saved.extraIncomes.length > 0) {
                setExtraIncomes(
                    saved.extraIncomes.map((item) => ({
                        ...item,
                        id: Math.random().toString(36).slice(2),
                    }))
                );
            } else {
                setExtraIncomes([createEmptyExtraIncome()]);
            }
        } catch {
            setError("Не удалось сохранить данные за месяц. Попробуйте ещё раз.");
        } finally {
            setIsSaving(false);
        }
    };

    // --- обработчики доп. доходов ---
    const handleExtraLabelChange = (id: string, value: string) => {
        setExtraIncomes((prev) =>
            prev.map((item) => (item.id === id ? { ...item, label: value } : item))
        );
    };

    const handleExtraAmountChange = (id: string, value: string) => {
        const numeric = Number(value.replace(",", "."));
        setExtraIncomes((prev) =>
            prev.map((item) =>
                item.id === id
                    ? { ...item, amount: Number.isNaN(numeric) ? 0 : numeric }
                    : item
            )
        );
    };

    const handleAddExtraIncome = () => {
        setExtraIncomes((prev) => [...prev, createEmptyExtraIncome()]);
    };

    const handleRemoveExtraIncome = (id: string) => {
        setExtraIncomes((prev) =>
            prev.length === 1 ? prev : prev.filter((item) => item.id !== id)
        );
    };

    // --- данные для графиков ---

    // Линия: доход по месяцам
    const lineChartData = useMemo(() => {
        const history = serverResult?.history ?? [];

        const hasCurrentInHistory = history.some(
            (p) => p.year === year && p.month === month
        );

        const allPoints = hasCurrentInHistory
            ? history
            : history.concat({
                year,
                month,
                totalMonthlyIncome: effectiveTotalMonthlyIncome,
            });

        return allPoints
            .slice()
            .sort((a, b) =>
                a.year === b.year ? a.month - b.month : a.year - b.year
            )
            .map((point) => ({
                name: `${MONTHS_RU[point.month - 1].slice(0, 3)} ${String(
                    point.year
                ).slice(2)}`,
                value: point.totalMonthlyIncome,
            }));
    }, [serverResult, year, month, effectiveTotalMonthlyIncome]);

    // Круговая: структура дохода
    const pieChartData = useMemo(() => {
        const segments = [
            {
                name: "Зарплата",
                value: salaryMonthly,
            },
            {
                name: "Дивиденды (мес.)",
                value: effectiveDividendsMonthly,
            },
            {
                name: "Вклад (мес.)",
                value: effectiveDepositInterestMonthly,
            },
            {
                name: "Доп. доходы",
                value: effectiveExtraMonthly,
            },
        ].filter((segment) => segment.value > 0.01);

        return segments.length > 0 ? segments : [{ name: "Нет данных", value: 1 }];
    }, [
        salaryMonthly,
        effectiveDividendsMonthly,
        effectiveDepositInterestMonthly,
        effectiveExtraMonthly,
    ]);

    return (
        <div className="budget-income-page">
            <div className="budget-income-header">
                <div>
                    <h1 className="budget-income-title">Месячная доходность</h1>
                    <p className="budget-income-subtitle">
                        Считаем, сколько реально приходит в месяц: зарплата, дивиденды,
                        вклад и дополнительные источники.
                    </p>
                </div>

                <div className="budget-income-period">
                    <div className="field-group">
                        <label className="field-label">Месяц</label>
                        <select
                            className="field-select"
                            value={month}
                            onChange={(event) => setMonth(Number(event.target.value))}
                        >
                            {MONTHS_RU.map((name, index) => (
                                <option key={name} value={index + 1}>
                                    {name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="field-group">
                        <label className="field-label">Год</label>
                        <input
                            className="field-input"
                            type="number"
                            value={year}
                            onChange={(event) => setYear(Number(event.target.value))}
                            min={2000}
                            max={2100}
                        />
                    </div>
                </div>
            </div>

            {error && <div className="budget-income-error">{error}</div>}

            <div className="budget-income-grid">
                {/* Левая колонка: форма */}
                <section className="income-card income-card--form">
                    <h2 className="income-card-title">Входные данные месяца</h2>

                    {isLoading && (
                        <div className="income-loading">Загружаем данные за месяц…</div>
                    )}

                    <div className="income-form-grid">
                        <div className="field-group">
                            <label className="field-label">Годовые дивиденды, ₽</label>
                            <input
                                className="field-input"
                                type="number"
                                value={yearlyDividends}
                                onChange={(event) =>
                                    setYearlyDividends(Number(event.target.value || 0))
                                }
                                min={0}
                            />
                        </div>

                        <div className="field-group">
                            <label className="field-label">Сумма на вкладе, ₽</label>
                            <input
                                className="field-input"
                                type="number"
                                value={depositAmount}
                                onChange={(event) =>
                                    setDepositAmount(Number(event.target.value || 0))
                                }
                                min={0}
                            />
                        </div>

                        <div className="field-group">
                            <label className="field-label">Ставка по вкладу, % годовых</label>
                            <input
                                className="field-input"
                                type="number"
                                value={depositRateYearly}
                                onChange={(event) =>
                                    setDepositRateYearly(Number(event.target.value || 0))
                                }
                                min={0}
                                step={0.1}
                            />
                        </div>

                        <div className="field-group">
                            <label className="field-label">Зарплата за месяц, ₽</label>
                            <input
                                className="field-input"
                                type="number"
                                value={salaryMonthly}
                                onChange={(event) =>
                                    setSalaryMonthly(Number(event.target.value || 0))
                                }
                                min={0}
                            />
                        </div>
                    </div>

                    <div className="income-extra-header">
                        <h3 className="income-extra-title">Дополнительные доходы</h3>
                        <button
                            type="button"
                            className="btn-ghost"
                            onClick={handleAddExtraIncome}
                        >
                            + Добавить доход
                        </button>
                    </div>

                    <div className="income-extra-list">
                        {extraIncomes.map((item, index) => (
                            <div key={item.id} className="income-extra-row">
                                <div className="field-group field-group--flex">
                                    <label className="field-label">
                                        Название
                                        {index === 0 && " (например, аренда, фриланс)"}
                                    </label>
                                    <input
                                        className="field-input"
                                        type="text"
                                        value={item.label}
                                        onChange={(event) =>
                                            handleExtraLabelChange(item.id, event.target.value)
                                        }
                                        placeholder="Источник дохода"
                                    />
                                </div>

                                <div className="field-group field-group--flex">
                                    <label className="field-label">Сумма в месяц, ₽</label>
                                    <input
                                        className="field-input"
                                        type="number"
                                        value={item.amount}
                                        onChange={(event) =>
                                            handleExtraAmountChange(item.id, event.target.value)
                                        }
                                        min={0}
                                    />
                                </div>

                                <button
                                    type="button"
                                    className="btn-icon"
                                    onClick={() => handleRemoveExtraIncome(item.id)}
                                    disabled={extraIncomes.length === 1}
                                    title="Удалить доход"
                                >
                                    ✕
                                </button>
                            </div>
                        ))}
                    </div>

                    <div className="income-actions">
                        <button
                            type="button"
                            className="btn-primary"
                            onClick={handleSave}
                            disabled={isSaving}
                        >
                            {isSaving ? "Сохраняем…" : "Сохранить месяц"}
                        </button>
                    </div>
                </section>

                {/* Правая колонка: график слева, статус дохода справа + структура */}
                <section className="income-card income-card--summary">
                    <h2 className="income-card-title">Итоги месяца</h2>

                    {/* Верхний блок: слева график, справа статус дохода */}
                    <div className="income-summary-layout">
                        <div className="income-chart-box income-chart-box--main">
                            <h3 className="income-chart-title">Доход по месяцам</h3>
                            <ResponsiveContainer width="100%" height={220}>
                                <LineChart data={lineChartData}>
                                    <XAxis dataKey="name" />
                                    <YAxis />
                                    <Tooltip
                                        formatter={(value: number) =>
                                            `${Number(value).toLocaleString("ru-RU")} ₽`
                                        }
                                    />
                                    <Line
                                        type="monotone"
                                        dataKey="value"
                                        stroke="#f6e27a"
                                        strokeWidth={2}
                                        dot={{ r: 3 }}
                                    />
                                </LineChart>
                            </ResponsiveContainer>
                        </div>

                        <div className="income-summary-grid">
                            <div className="income-summary-block">
                                <div className="income-summary-label">Зарплата</div>
                                <div className="income-summary-value">
                                    {salaryMonthly.toLocaleString("ru-RU")} ₽
                                </div>
                            </div>

                            <div className="income-summary-block">
                                <div className="income-summary-label">
                                    Дивиденды в месяц
                                </div>
                                <div className="income-summary-value">
                                    {effectiveDividendsMonthly.toLocaleString("ru-RU", {
                                        maximumFractionDigits: 0,
                                    })}{" "}
                                    ₽
                                </div>
                            </div>

                            <div className="income-summary-block">
                                <div className="income-summary-label">
                                    Процент со вклада в месяц
                                </div>
                                <div className="income-summary-value">
                                    {effectiveDepositInterestMonthly.toLocaleString(
                                        "ru-RU",
                                        {
                                            maximumFractionDigits: 0,
                                        }
                                    )}{" "}
                                    ₽
                                </div>
                            </div>

                            <div className="income-summary-block">
                                <div className="income-summary-label">
                                    Дополнительные доходы
                                </div>
                                <div className="income-summary-value">
                                    {effectiveExtraMonthly.toLocaleString("ru-RU", {
                                        maximumFractionDigits: 0,
                                    })}{" "}
                                    ₽
                                </div>
                            </div>

                            <div className="income-summary-block income-summary-block--total">
                                <div className="income-summary-label">
                                    Итого за месяц
                                </div>
                                <div className="income-summary-value income-summary-value--accent">
                                    {effectiveTotalMonthlyIncome.toLocaleString("ru-RU", {
                                        maximumFractionDigits: 0,
                                    })}{" "}
                                    ₽
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* Нижний блок: структура дохода (pie) */}
                    <div className="income-charts">
                        <div className="income-chart-box">
                            <h3 className="income-chart-title">Структура дохода</h3>
                            <ResponsiveContainer width="100%" height={220}>
                                <PieChart>
                                    <Pie
                                        data={pieChartData}
                                        dataKey="value"
                                        nameKey="name"
                                        innerRadius={50}
                                        outerRadius={80}
                                        paddingAngle={3}
                                    >
                                        {pieChartData.map((_, index) => (
                                            <Cell
                                                key={`cell-${index}`}
                                                fill={PIE_COLORS[index % PIE_COLORS.length]}
                                            />
                                        ))}
                                    </Pie>
                                    <Tooltip
                                        formatter={(value: number) =>
                                            `${Number(value).toLocaleString("ru-RU")} ₽`
                                        }
                                    />
                                    <Legend />
                                </PieChart>
                            </ResponsiveContainer>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    );
};

export default BudgetIncomePage;
