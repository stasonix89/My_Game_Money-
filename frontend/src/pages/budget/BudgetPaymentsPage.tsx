// src/pages/budget/BudgetPaymentsPage.tsx
import React, { useEffect, useMemo, useState } from "react";
import {
    fetchMonthlyPayments,
    createMonthlyPayment,
    type MonthlyPaymentDto,
} from "../../api/budgetPaymentsApi";
import { fetchAccounts } from "../../api/budgetAccountsApi";
import type { AccountDto } from "../../types/budget/accounts";
import "./budget-payments.css";

// Тип для основной карты
export interface MainAccountDto {
    id: number;
    bankName: string;
    name: string;
    type: string;
    balance: number;
    mainForPayments: boolean;
}

// Платёж, в котором backend может вернуть информацию о счёте
type MonthlyPaymentWithAccountInfo = MonthlyPaymentDto & {
    accountId?: number | null;
    account?: {
        id: number;
    };
};

const pad2 = (n: number) => n.toString().padStart(2, "0");

const BudgetPaymentsPage: React.FC = () => {
    const [payments, setPayments] = useState<MonthlyPaymentDto[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [selectedMonth, setSelectedMonth] = useState<number>(
        new Date().getMonth() + 1
    ); // 1–12
    const [selectedYear, setSelectedYear] = useState<number>(
        new Date().getFullYear()
    );
    const [showOnlyUnpaid, setShowOnlyUnpaid] = useState<boolean>(true);

    // Состояние для модалки "Добавить платёж"
    const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
    const [newAmount, setNewAmount] = useState("");
    const [newDay, setNewDay] = useState<string>("1");
    const [newCreditAccountId, setNewCreditAccountId] = useState<
        number | null
    >(null);

    const [isSaving, setIsSaving] = useState(false);
    const [mainAccount, setMainAccount] = useState<MainAccountDto | null>(null);

    // Счета (для выбора кредитных карт)
    const [accounts, setAccounts] = useState<AccountDto[]>([]);
    const [accountsLoading, setAccountsLoading] = useState(false);
    const [accountsError, setAccountsError] = useState<string | null>(null);

    // Загрузка платежей
    useEffect(() => {
        const loadPayments = async () => {
            try {
                setIsLoading(true);
                setError(null);
                const data = await fetchMonthlyPayments();
                setPayments(data);
            } catch (e) {
                console.error(e);
                setError("Не удалось загрузить список платежей");
            } finally {
                setIsLoading(false);
            }
        };

        void loadPayments();
    }, []);

    // Загрузка основной карты для платежей
    useEffect(() => {
        const loadMainAccount = async () => {
            try {
                const response = await fetch(
                    "/api/budget/accounts/main-for-payments"
                );
                if (response.ok) {
                    const data = await response.json();
                    setMainAccount(data);
                } else {
                    setMainAccount(null);
                }
            } catch (e) {
                console.error(e);
                setMainAccount(null);
            }
        };

        void loadMainAccount();
    }, []);

    // Загрузка всех счетов (для выбора кредитных карт)
    useEffect(() => {
        const loadAccounts = async () => {
            try {
                setAccountsLoading(true);
                setAccountsError(null);
                const data = await fetchAccounts();
                setAccounts(data);
            } catch (e) {
                console.error(e);
                setAccountsError("Не удалось загрузить счета");
            } finally {
                setAccountsLoading(false);
            }
        };

        void loadAccounts();
    }, []);

    // Только кредитные карты (куда идёт погашение обязательств)
    const creditAccounts = useMemo(
        () =>
            accounts.filter(
                (a) =>
                    a.type === "CREDIT_PURCHASE" ||
                    a.type === "CREDIT_CASH"
            ),
        [accounts]
    );

    // Для отображения в таблице
    const accountById = useMemo(() => {
        const map = new Map<number, AccountDto>();
        accounts.forEach((a) => map.set(a.id, a));
        return map;
    }, [accounts]);

    const formatAccount = (acc: AccountDto | undefined): string => {
        if (!acc) return "";
        return `${acc.bankName} — ${acc.name}`;
    };

    // Отфильтровать платежи по месяцам, годам и статусу
    const filteredPayments = payments.filter((p) => {
        const date = new Date(p.paymentDate);
        const sameMonth = date.getMonth() + 1 === selectedMonth;
        const sameYear = date.getFullYear() === selectedYear;
        const unpaidOk = !showOnlyUnpaid || p.status !== "PAID";
        return sameMonth && sameYear && unpaidOk;
    });

    // Обработчик создания платежа
    const handleCreateSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        const amountNumber = Number(newAmount.replace(",", ".")); // 10,5 → 10.5
        if (!amountNumber || amountNumber <= 0) {
            setError("Сумма должна быть положительным числом");
            return;
        }

        if (!mainAccount) {
            setError(
                "Основная карта для списаний не настроена. Настрой её в разделе «Счета и карты»."
            );
            return;
        }

        if (!newCreditAccountId) {
            setError("Выберите кредитную карту, на которую идёт погашение.");
            return;
        }

        const numericDay = Number(newDay);
        if (
            !newDay ||
            Number.isNaN(numericDay) ||
            numericDay <= 0 ||
            numericDay > 31
        ) {
            setError("Укажите корректный день месяца (1–31).");
            return;
        }

        const paymentDate = `${selectedYear}-${pad2(
            selectedMonth
        )}-${pad2(numericDay)}`;

        try {
            setIsSaving(true);

            // Название платежа фиксированное, категория на стороне backend/транзакции.
            const created = await createMonthlyPayment({
                accountId: newCreditAccountId, // здесь хранится КРЕДИТНАЯ карта (куда платим)
                paymentDate,
                amount: amountNumber,
                title: "Погашение обязательств",
            });

            setPayments((prev) => [...prev, created]);
            setIsCreateModalOpen(false);
            setNewAmount("");
            setNewDay("1");
            setNewCreditAccountId(null);
        } catch (e) {
            console.error(e);
            setError("Не удалось создать платёж");
        } finally {
            setIsSaving(false);
        }
    };

    const handleOpenCreateModal = () => {
        setError(null);
        setNewAmount("");
        setNewDay("1");
        setNewCreditAccountId(
            creditAccounts.length > 0 ? creditAccounts[0].id : null
        );
        setIsCreateModalOpen(true);
    };

    const handleExportPdf = () => {
        const params = new URLSearchParams({
            year: String(selectedYear),
            month: String(selectedMonth),
            onlyUnpaid: String(showOnlyUnpaid),
        });
        window.open(
            `/api/budget/payments/export?${params.toString()}`,
            "_blank"
        );
    };

    const canCreatePayment = !!mainAccount && creditAccounts.length > 0;

    return (
        <div className="budget-payments-page">
            {/* Заголовок */}
            <div className="budget-page-header">
                <div>
                    <h1>Месячные платежи</h1>
                    <span className="hint">
                        Оплаченные платежи автоматически превращаются в
                        операции бюджета (перевод с основной карты на
                        кредитную карту).
                    </span>
                </div>

                <div className="main-account-info">
                    {mainAccount ? (
                        <span className="main-account-label">
                            Основная карта для списаний:{" "}
                            <strong>
                                {mainAccount.bankName} — {mainAccount.name}
                            </strong>
                        </span>
                    ) : (
                        <span className="main-account-warning">
                            Основная карта для списаний не настроена. Настрой её
                            в разделе «Счета и карты».
                        </span>
                    )}
                </div>
            </div>

            {/* Панель фильтров + кнопки */}
            <div className="payments-toolbar">
                <div className="filters">
                    <select
                        value={selectedMonth}
                        onChange={(e) =>
                            setSelectedMonth(Number(e.target.value))
                        }
                    >
                        <option value={1}>Январь</option>
                        <option value={2}>Февраль</option>
                        <option value={3}>Март</option>
                        <option value={4}>Апрель</option>
                        <option value={5}>Май</option>
                        <option value={6}>Июнь</option>
                        <option value={7}>Июль</option>
                        <option value={8}>Август</option>
                        <option value={9}>Сентябрь</option>
                        <option value={10}>Октябрь</option>
                        <option value={11}>Ноябрь</option>
                        <option value={12}>Декабрь</option>
                    </select>

                    <select
                        value={selectedYear}
                        onChange={(e) =>
                            setSelectedYear(Number(e.target.value))
                        }
                    >
                        {[new Date().getFullYear(), new Date().getFullYear() - 1].map(
                            (year) => (
                                <option key={year} value={year}>
                                    {year}
                                </option>
                            )
                        )}
                    </select>

                    <select
                        value={showOnlyUnpaid ? "unpaid" : "all"}
                        onChange={(e) =>
                            setShowOnlyUnpaid(e.target.value === "unpaid")
                        }
                    >
                        <option value="all">Все платежи</option>
                        <option value="unpaid">Только неоплаченные</option>
                    </select>
                </div>

                <div className="toolbar-actions">
                    <button
                        type="button"
                        className="btn btn-secondary"
                        onClick={handleExportPdf}
                    >
                        Скачать PDF
                    </button>

                    <button
                        type="button"
                        className="btn btn-add-payment"
                        onClick={handleOpenCreateModal}
                        disabled={!canCreatePayment}
                        title={
                            canCreatePayment
                                ? ""
                                : !mainAccount
                                    ? "Сначала настрой основную дебетовую карту для списаний в разделе «Счета и карты»."
                                    : "Нужна хотя бы одна кредитная карта, чтобы создать платеж."
                        }
                    >
                        + Добавить платёж
                    </button>
                </div>
            </div>

            {isLoading && <div className="loading">Загрузка...</div>}
            {accountsLoading && (
                <div className="loading">
                    Загрузка счетов (кредитные карты)...
                </div>
            )}
            {(error || accountsError) && (
                <div className="error">{error || accountsError}</div>
            )}

            <table className="payments-table">
                {filteredPayments.length > 0 ? (
                    <>
                        <thead>
                        <tr>
                            <th>Кредитная карта</th>
                            <th>Назначение</th>
                            <th>Сумма</th>
                            <th>Дата</th>
                            <th>Статус</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredPayments.map((p) => {
                            const mp = p as MonthlyPaymentWithAccountInfo;

                            let acc: AccountDto | undefined;
                            if (mp.accountId != null) {
                                acc = accountById.get(mp.accountId);
                            } else if (mp.account?.id != null) {
                                acc = accountById.get(mp.account.id);
                            }

                            return (
                                <tr
                                    key={p.id}
                                    className={
                                        p.status === "PAID"
                                            ? "row-paid"
                                            : "row-planned"
                                    }
                                >
                                    <td>
                                        {acc
                                            ? formatAccount(acc)
                                            : "Кредитная карта"}
                                    </td>
                                    <td>Погашение обязательств</td>
                                    <td>
                                        {p.amount.toLocaleString(
                                            "ru-RU"
                                        )}{" "}
                                        ₽
                                    </td>
                                    <td>
                                        {new Date(
                                            p.paymentDate
                                        ).toLocaleDateString("ru-RU")}
                                    </td>
                                    <td>
                                            <span
                                                className={`status-badge ${
                                                    p.status === "PAID"
                                                        ? "paid"
                                                        : "planned"
                                                }`}
                                            >
                                                {p.status === "PAID"
                                                    ? "Оплачен"
                                                    : "Запланирован"}
                                            </span>
                                    </td>
                                </tr>
                            );
                        })}
                        </tbody>
                    </>
                ) : (
                    <tbody>
                    <tr>
                        <td colSpan={5} className="empty-row">
                            Нет платежей для отображения
                        </td>
                    </tr>
                    </tbody>
                )}
            </table>

            {/* Модалка для добавления платежа */}
            {isCreateModalOpen && (
                <div
                    className="modal-backdrop"
                    onClick={() => setIsCreateModalOpen(false)}
                >
                    <div
                        className="modal"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <h2>Добавить платёж</h2>
                        <form
                            onSubmit={handleCreateSubmit}
                            className="modal-form"
                        >
                            <div className="modal-field">
                                <label>Сумма</label>
                                <input
                                    type="text"
                                    value={newAmount}
                                    onChange={(e) =>
                                        setNewAmount(e.target.value)
                                    }
                                    placeholder="45000"
                                />
                            </div>

                            <div className="modal-field">
                                <label>Дата платежа</label>
                                <div className="date-inline">
                                    <input
                                        type="number"
                                        min={1}
                                        max={31}
                                        value={newDay}
                                        onChange={(e) =>
                                            setNewDay(e.target.value)
                                        }
                                        style={{ maxWidth: 80 }}
                                    />
                                    <span className="date-suffix">
                                        .{pad2(selectedMonth)}.
                                        {selectedYear}
                                    </span>
                                </div>
                                <div className="field-hint">
                                    Месяц и год берутся из фильтров сверху.
                                    Здесь меняется только число.
                                </div>
                            </div>

                            <div className="modal-field">
                                <label>Основная карта (откуда списываем)</label>
                                {mainAccount ? (
                                    <input
                                        type="text"
                                        value={`${mainAccount.bankName} — ${mainAccount.name}`}
                                        disabled
                                    />
                                ) : (
                                    <span className="error">
                                        Основная карта не найдена. Настрой её в
                                        разделе «Счета и карты».
                                    </span>
                                )}
                            </div>

                            <div className="modal-field">
                                <label>Кредитная карта (куда погашаем)</label>
                                {creditAccounts.length > 0 ? (
                                    <select
                                        value={
                                            newCreditAccountId ?? ""
                                        }
                                        onChange={(e) =>
                                            setNewCreditAccountId(
                                                e.target.value
                                                    ? Number(
                                                        e.target.value
                                                    )
                                                    : null
                                            )
                                        }
                                    >
                                        <option value="">
                                            — выбери кредитную карту —
                                        </option>
                                        {creditAccounts.map((acc) => (
                                            <option
                                                key={acc.id}
                                                value={acc.id}
                                            >
                                                {acc.bankName} — {acc.name}
                                            </option>
                                        ))}
                                    </select>
                                ) : (
                                    <span className="error">
                                        Нет кредитных карт. Создай их в разделе
                                        «Счета и карты».
                                    </span>
                                )}
                            </div>

                            <div className="modal-actions">
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() =>
                                        setIsCreateModalOpen(false)
                                    }
                                    disabled={isSaving}
                                >
                                    Отмена
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-add-payment primary"
                                    disabled={
                                        isSaving || !canCreatePayment
                                    }
                                >
                                    {isSaving
                                        ? "Сохранение..."
                                        : "Сохранить"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default BudgetPaymentsPage;
