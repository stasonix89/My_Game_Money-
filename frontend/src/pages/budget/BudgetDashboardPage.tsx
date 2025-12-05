// src/pages/budget/BudgetDashboardPage.tsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../budget/budget-dashboard.css";
import { getBudgetDashboard } from "../../api/budgetApi";

/**
 * Типы под текущий JSON от backend.
 * Если в budgetApi.ts у тебя уже есть такие интерфейсы – можешь их
 * импортировать оттуда и эти объявления удалить.
 */

type FinanceWidget = {
    totalDebitAndAssets: number;   // все дебетовые карты + активы
    totalCreditForCash: number;    // задолженность по кредиткам для снятия
    netWorth: number;              // Баланс = дебетовые карты − задолженность
};

type IncomeHistoryPoint = {
    year: number;
    month: number;                // 1-12
    totalMonthlyIncome: number;
};

type IncomeWidget = {
    currentMonthIncome: number;
    previousMonthIncome: number;
    dividendsMonthly: number;
    depositInterestMonthly: number;
    extraIncomesMonthly: number;
    history: IncomeHistoryPoint[];
};

type PaymentRow = {
    id: number;
    title: string;
    amount: number;
    paid: boolean;
};

type PaymentsWidget = {
    rows: PaymentRow[];
    totalPlanned: number;
    totalPaid: number;
};

export type BudgetDashboardResponse = {
    finance: FinanceWidget;
    income: IncomeWidget;
    payments: PaymentsWidget;
};

const BudgetDashboardPage: React.FC = () => {
    const navigate = useNavigate();

    const [data, setData] = useState<BudgetDashboardResponse | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        let cancelled = false;

        const loadDashboard = async () => {
            try {
                setLoading(true);
                setError(null);

                const response = await getBudgetDashboard(); // берём текущий месяц
                if (!cancelled) {
                    setData(response);
                }
            } catch (e) {
                console.error("Failed to load dashboard", e);
                if (!cancelled) {
                    setError("Не удалось загрузить данные дашборда.");
                }
            } finally {
                if (!cancelled) {
                    setLoading(false);
                }
            }
        };

        void loadDashboard();

        return () => {
            cancelled = true;
        };
    }, []);

    const handleOpenFinance = () => {
        navigate("/budget/finance");
    };

    const handleOpenIncome = () => {
        navigate("/budget/income");
    };

    const handleOpenPayments = () => {
        navigate("/budget/payments");
    };

    // --------- состояния загрузки / ошибки ---------

    if (loading) {
        return (
            <div className="page-container">
                <h1 className="gold-title gold-title-line page-title">
                    Бюджет — общий дашборд
                </h1>
                <p className="page-description">Загружаем данные…</p>
            </div>
        );
    }

    if (error || !data) {
        return (
            <div className="page-container">
                <h1 className="gold-title gold-title-line page-title">
                    Бюджет — общий дашборд
                </h1>
                <p className="page-description error-text">
                    {error ?? "Нет данных"}
                </p>
            </div>
        );
    }

    // ---------- нормальный рендер ----------

    const { finance, income, payments } = data;

    // Безопасные значения (если вдруг что-то не придёт)
    const totalAssets = finance?.totalDebitAndAssets ?? 0;          // дебет + активы
    const totalCreditCarousel = finance?.totalCreditForCash ?? 0;   // задолженность по кредиткам для снятия
    const netBalance = finance?.netWorth ?? 0;                       // Баланс = дебетовые − задолженность

    const previousMonthIncome = income?.previousMonthIncome ?? 0;
    const dividendsPerMonth = income?.dividendsMonthly ?? 0;
    const depositInterestPerMonth = income?.depositInterestMonthly ?? 0;

    const totalPlanned = payments?.totalPlanned ?? 0;
    const totalPaid = payments?.totalPaid ?? 0;
    const remaining = totalPlanned - totalPaid;

    // мини-серия для графика дохода
    const monthlyIncomeSeries =
        income?.history?.map((p) => ({
            month: `${p.month}.${p.year}`,
            value: p.totalMonthlyIncome,
        })) ?? [];

    const paymentRows = payments?.rows ?? [];

    // визуал для баланса: зелёная / красная полоса
    const balancePositive = netBalance >= 0;
    const balanceBarWidth = (() => {
        if (totalAssets <= 0) return 10;
        const raw = Math.abs((netBalance / totalAssets) * 100);
        return Math.min(100, Math.max(10, raw)); // минимум 10%, максимум 100%
    })();

    return (
        <div className="page-container budget-dashboard">
            {/* Заголовок страницы */}
            <div className="budget-header">
                <div>
                    <h1 className="gold-title gold-title-line page-title">
                        Бюджет — общий дашборд
                    </h1>
                    <p className="page-description">
                        Краткий обзор: Учёт финансов, Месячная доходность и Месячные
                        платежи за текущий период.
                    </p>
                </div>
                {/* сюда потом можно добавить выбор месяца */}
            </div>

            {/* Сетка виджетов */}
            <div className="budget-grid">
                {/* ВИДЖЕТ 1 — Учёт финансов */}
                <section className="widget-card widget-finance">
                    <div className="widget-header">
                        <h2 className="widget-title">Учёт финансов</h2>
                        <button
                            className="widget-link-button"
                            type="button"
                            onClick={handleOpenFinance}
                        >
                            Открыть детали
                        </button>
                    </div>

                    <div className="widget-body">
                        <div className="widget-stat-row">
                            <span className="widget-stat-label">Баланс</span>
                            <span className="widget-stat-value">
                                {netBalance.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>

                        <div className="widget-stat-row">
                            <span className="widget-stat-label">Активы</span>
                            <span className="widget-stat-value">
                                {totalAssets.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>

                        <div className="widget-stat-row">
                            <span className="widget-stat-label">
                                Задолженность (кредитная карусель)
                            </span>
                            <span className="widget-stat-value negative">
                                −{totalCreditCarousel.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>

                        <div className="widget-divider" />

                        {/* Полоса баланса: зелёная / красная, число внутри */}
                        <div
                            className={
                                "finance-balance-bar " +
                                (balancePositive
                                    ? "finance-balance-bar-positive"
                                    : "finance-balance-bar-negative")
                            }
                        >
                            <div
                                className="finance-balance-bar-fill"
                                style={{ width: `${balanceBarWidth}%` }}
                            />
                            <span className="finance-balance-bar-label">
                                {netBalance.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>
                    </div>
                </section>

                {/* ВИДЖЕТ 2 — Месячная доходность */}
                <section className="widget-card widget-income">
                    <div className="widget-header">
                        <h2 className="widget-title">Месячная доходность</h2>
                        <button
                            className="widget-link-button"
                            type="button"
                            onClick={handleOpenIncome}
                        >
                            Перейти к доходам
                        </button>
                    </div>

                    <div className="widget-body">
                        <div className="widget-stat-row">
                            <span className="widget-stat-label">
                                Доход за прошлый месяц
                            </span>
                            <span className="widget-stat-value">
                                {previousMonthIncome.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>

                        <div className="widget-stat-row">
                            <span className="widget-stat-label">Дивиденды в месяц</span>
                            <span className="widget-stat-value">
                                {dividendsPerMonth.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>

                        <div className="widget-stat-row">
                            <span className="widget-stat-label">
                                Проценты по вкладам в месяц
                            </span>
                            <span className="widget-stat-value">
                                {depositInterestPerMonth.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>

                        <div className="widget-divider" />

                        {/* Простой мини-график дохода */}
                        <div className="income-mini-chart">
                            {monthlyIncomeSeries.map((p) => (
                                <div
                                    key={p.month}
                                    className="income-bar"
                                    title={`${p.month}: ${p.value.toLocaleString(
                                        "ru-RU"
                                    )} ₽`}
                                    style={{
                                        height: `${Math.max(p.value, 1) / 1000}%`.replace(
                                            "%%",
                                            "%"
                                        ),
                                    }}
                                />
                            ))}
                        </div>

                        <div className="income-mini-chart-caption">
                            Динамика доходов по месяцам
                        </div>
                    </div>
                </section>

                {/* ВИДЖЕТ 3 — Месячные платежи */}
                <section className="widget-card widget-payments">
                    <div className="widget-header">
                        <h2 className="widget-title">Месячные платежи</h2>
                        <button
                            className="widget-link-button"
                            type="button"
                            onClick={handleOpenPayments}
                        >
                            Открыть таблицу
                        </button>
                    </div>

                    <div className="widget-body">
                        <div className="widget-stat-row">
                            <span className="widget-stat-label">
                                Итого к оплате за месяц
                            </span>
                            <span className="widget-stat-value">
                                {totalPlanned.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>

                        <div className="widget-stat-row">
                            <span className="widget-stat-label">Уже оплачено</span>
                            <span className="widget-stat-value positive">
                                {totalPaid.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>

                        <div className="widget-stat-row">
                            <span className="widget-stat-label">
                                Осталось оплатить
                            </span>
                            <span className="widget-stat-value">
                                {remaining.toLocaleString("ru-RU")} ₽
                            </span>
                        </div>

                        <div className="widget-divider" />

                        <div className="payments-table-preview">
                            <div className="payments-table-header">
                                <span>Название платежа</span>
                                <span>Сумма</span>
                                <span>Статус</span>
                            </div>

                            {paymentRows.slice(0, 4).map((p) => (
                                <div key={p.id} className="payments-table-row">
                                    <span className="payments-cell-main">
                                        <span className="payments-bank">{p.title}</span>
                                    </span>
                                    <span>
                                        {p.amount.toLocaleString("ru-RU")} ₽
                                    </span>
                                    <span
                                        className={p.paid ? "tag-paid" : "tag-planned"}
                                    >
                                        {p.paid ? "Оплачено" : "Запланировано"}
                                    </span>
                                </div>
                            ))}

                            {paymentRows.length > 4 && (
                                <div className="payments-more">
                                    + ещё {paymentRows.length - 4} платеж(а/ей) в этом
                                    месяце
                                </div>
                            )}
                        </div>
                    </div>
                </section>
            </div>
        </div>
    );
};

export default BudgetDashboardPage;
