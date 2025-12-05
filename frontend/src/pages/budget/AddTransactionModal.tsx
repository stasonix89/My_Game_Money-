// src/pages/budget/AddTransactionModal.tsx
import React, { useMemo, useState, type FormEvent } from "react";
import type { AccountDto } from "../../types/budget/accounts";
import type { CategoryDto, OperationKind } from "../../types/budget/categories";

export type CreateTransactionPayload = {
    date: string;
    type: OperationKind; // "INCOME" | "EXPENSE" | "TRANSFER"
    category: string; // имя категории, как в TransactionDto
    accountFromId: number | null;
    accountToId: number | null;
    amount: number;
};

type AddTransactionModalProps = {
    isOpen: boolean;
    onClose: () => void;

    // выбранный месяц/год из фильтров FinancePage
    year: number; // например, 2025
    month: number; // 1–12

    // Счета и категории приходят сверху, из BudgetFinancePage
    accounts: AccountDto[];
    categories: CategoryDto[];

    // Вызовется при сохранении
    onSubmit: (payload: CreateTransactionPayload) => Promise<void> | void;
};

const monthNamesRu = [
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

const pad2 = (n: number) => n.toString().padStart(2, "0");

const AddTransactionModal: React.FC<AddTransactionModalProps> = ({
                                                                     isOpen,
                                                                     onClose,
                                                                     year,
                                                                     month,
                                                                     accounts,
                                                                     categories,
                                                                     onSubmit,
                                                                 }) => {
    // храним только день, месяц/год — из пропсов
    const [day, setDay] = useState<string>(() => {
        const today = new Date();
        if (
            today.getFullYear() === year &&
            today.getMonth() + 1 === month
        ) {
            return String(today.getDate());
        }
        return "1";
    });

    const [type, setType] = useState<OperationKind>("INCOME");
    const [categoryId, setCategoryId] = useState<string>("");
    const [accountFromId, setAccountFromId] = useState<string>("");
    const [accountToId, setAccountToId] = useState<string>("");
    const [amount, setAmount] = useState<string>("");

    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // категории по выбранному типу операции
    const filteredCategories = useMemo(
        () => categories.filter((c) => c.operationType === type),
        [categories, type]
    );

    // пока без фильтрации по типам счетов — всё решает backend
    const incomeAccounts = useMemo(() => accounts, [accounts]);
    const expenseAccounts = useMemo(() => accounts, [accounts]);
    const transferFromAccounts = useMemo(() => accounts, [accounts]);
    const transferToAccounts = useMemo(() => accounts, [accounts]);

    const resetForm = () => {
        const today = new Date();
        if (
            today.getFullYear() === year &&
            today.getMonth() + 1 === month
        ) {
            setDay(String(today.getDate()));
        } else {
            setDay("1");
        }
        setType("INCOME");
        setCategoryId("");
        setAccountFromId("");
        setAccountToId("");
        setAmount("");
        setError(null);
    };

    const handleClose = () => {
        if (submitting) return;
        resetForm();
        onClose();
    };

    const buildDate = (): string | null => {
        const numericDay = Number(day);
        if (!day || Number.isNaN(numericDay) || numericDay <= 0 || numericDay > 31) {
            return null;
        }
        const yearStr = String(year);
        const monthStr = pad2(month);
        const dayStr = pad2(numericDay);
        return `${yearStr}-${monthStr}-${dayStr}`;
    };

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setError(null);

        const builtDate = buildDate();
        if (!builtDate) {
            setError("Укажи корректный день месяца (1–31).");
            return;
        }

        const numericAmount = Number(amount.replace(",", "."));
        if (!amount || Number.isNaN(numericAmount) || numericAmount <= 0) {
            setError("Сумма должна быть положительным числом.");
            return;
        }

        if (!categoryId) {
            setError("Выбери категорию.");
            return;
        }

        const category = categories.find((c) => c.id === Number(categoryId));
        if (!category) {
            setError("Категория не найдена.");
            return;
        }

        let accountFrom: number | null = null;
        let accountTo: number | null = null;

        if (type === "INCOME") {
            if (!accountToId) {
                setError("Выбери счёт, на который зачисляем доход.");
                return;
            }
            accountTo = Number(accountToId);
        }

        if (type === "EXPENSE") {
            if (!accountFromId) {
                setError("Выбери счёт, с которого списываем расход.");
                return;
            }
            accountFrom = Number(accountFromId);
        }

        if (type === "TRANSFER") {
            if (!accountFromId || !accountToId) {
                setError("Выбери оба счёта для перевода.");
                return;
            }
            if (accountFromId === accountToId) {
                setError("Нельзя переводить на тот же самый счёт.");
                return;
            }
            accountFrom = Number(accountFromId);
            accountTo = Number(accountToId);
        }

        const payload: CreateTransactionPayload = {
            date: builtDate,
            type,
            category: category.name,
            accountFromId: accountFrom,
            accountToId: accountTo,
            amount: numericAmount,
        };

        try {
            setSubmitting(true);
            await onSubmit(payload);
            resetForm();
            onClose();
        } catch (err) {
            console.error(err);
            setError("Не удалось сохранить операцию. Попробуй ещё раз.");
        } finally {
            setSubmitting(false);
        }
    };

    if (!isOpen) return null;

    const monthName =
        month >= 1 && month <= 12 ? monthNamesRu[month - 1] : "";

    return (
        <div className="modal-backdrop" onClick={handleClose}>
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <button
                    type="button"
                    className="btn btn-sm btn-secondary"
                    style={{ alignSelf: "flex-end", marginBottom: 4 }}
                    onClick={handleClose}
                >
                    ×
                </button>

                <h2 className="modal-title">Добавить операцию</h2>

                <form className="modal-form" onSubmit={handleSubmit}>
                    {/* Дата: меняется только день, месяц/год зафиксированы */}
                    <div className="modal-field">
                        <label htmlFor="tx-day">Дата</label>
                        <div
                            style={{
                                display: "flex",
                                alignItems: "center",
                                gap: 8,
                            }}
                        >
                            <input
                                id="tx-day"
                                type="number"
                                min={1}
                                max={31}
                                value={day}
                                onChange={(e) => setDay(e.target.value)}
                                style={{ maxWidth: 80 }}
                            />
                            <span style={{ opacity: 0.8 }}>
                                .{pad2(month)}.{year}
                            </span>
                        </div>
                        <div
                            style={{
                                fontSize: 12,
                                opacity: 0.7,
                                marginTop: 4,
                            }}
                        >
                            Месяц и год берутся из фильтра: {monthName}{" "}
                            {year}. Здесь меняется только день.
                        </div>
                    </div>

                    {/* Тип операции */}
                    <div className="modal-field">
                        <label>Тип операции</label>
                        <div style={{ display: "flex", gap: 8 }}>
                            <button
                                type="button"
                                className={
                                    "btn btn-sm" +
                                    (type === "INCOME" ? " btn-main" : "")
                                }
                                onClick={() => setType("INCOME")}
                            >
                                Доход
                            </button>
                            <button
                                type="button"
                                className={
                                    "btn btn-sm" +
                                    (type === "EXPENSE" ? " btn-main" : "")
                                }
                                onClick={() => setType("EXPENSE")}
                            >
                                Расход
                            </button>
                            <button
                                type="button"
                                className={
                                    "btn btn-sm" +
                                    (type === "TRANSFER" ? " btn-main" : "")
                                }
                                onClick={() => setType("TRANSFER")}
                            >
                                Перевод
                            </button>
                        </div>
                    </div>

                    {/* Категория */}
                    <div className="modal-field">
                        <label htmlFor="tx-category">Категория</label>
                        <select
                            id="tx-category"
                            value={categoryId}
                            onChange={(e) => setCategoryId(e.target.value)}
                        >
                            <option value="">— выбери категорию —</option>
                            {filteredCategories.map((cat) => (
                                <option key={cat.id} value={cat.id}>
                                    {cat.name}
                                </option>
                            ))}
                        </select>
                    </div>

                    {/* Счета — зависят от типа операции */}
                    {type === "INCOME" && (
                        <div className="modal-field">
                            <label htmlFor="tx-account-to">
                                Счёт (куда зачисляем)
                            </label>
                            <select
                                id="tx-account-to"
                                value={accountToId}
                                onChange={(e) =>
                                    setAccountToId(e.target.value)
                                }
                            >
                                <option value="">— выбери счёт —</option>
                                {incomeAccounts.map((acc) => (
                                    <option key={acc.id} value={acc.id}>
                                        {acc.bankName} — {acc.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                    )}

                    {type === "EXPENSE" && (
                        <div className="modal-field">
                            <label htmlFor="tx-account-from">
                                Счёт (откуда списываем)
                            </label>
                            <select
                                id="tx-account-from"
                                value={accountFromId}
                                onChange={(e) =>
                                    setAccountFromId(e.target.value)
                                }
                            >
                                <option value="">— выбери счёт —</option>
                                {expenseAccounts.map((acc) => (
                                    <option key={acc.id} value={acc.id}>
                                        {acc.bankName} — {acc.name}
                                    </option>
                                ))}
                            </select>
                        </div>
                    )}

                    {type === "TRANSFER" && (
                        <div className="modal-field">
                            <label>Счета для перевода</label>
                            <div
                                style={{
                                    display: "grid",
                                    gridTemplateColumns: "1fr 1fr",
                                    gap: 8,
                                }}
                            >
                                <div>
                                    <span className="field-label">
                                        ОТКУДА
                                    </span>
                                    <select
                                        value={accountFromId}
                                        onChange={(e) =>
                                            setAccountFromId(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            — выбери счёт —
                                        </option>
                                        {transferFromAccounts.map((acc) => (
                                            <option
                                                key={acc.id}
                                                value={acc.id}
                                            >
                                                {acc.bankName} — {acc.name}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                                <div>
                                    <span className="field-label">
                                        КУДА
                                    </span>
                                    <select
                                        value={accountToId}
                                        onChange={(e) =>
                                            setAccountToId(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            — выбери счёт —
                                        </option>
                                        {transferToAccounts.map((acc) => (
                                            <option
                                                key={acc.id}
                                                value={acc.id}
                                            >
                                                {acc.bankName} — {acc.name}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>
                        </div>
                    )}

                    {/* Сумма */}
                    <div className="modal-field">
                        <label htmlFor="tx-amount">Сумма</label>
                        <input
                            id="tx-amount"
                            type="text"
                            value={amount}
                            onChange={(e) => setAmount(e.target.value)}
                            placeholder="Например, 8200"
                        />
                    </div>

                    {/* Ошибка */}
                    {error && <div className="error-row">{error}</div>}

                    {/* Кнопки */}
                    <div className="modal-actions">
                        <button
                            type="button"
                            className="btn btn-secondary"
                            onClick={handleClose}
                            disabled={submitting}
                        >
                            Отмена
                        </button>
                        <button
                            type="submit"
                            className="btn btn-add-account primary"
                            disabled={submitting}
                        >
                            {submitting ? "Сохраняем..." : "Сохранить"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AddTransactionModal;
