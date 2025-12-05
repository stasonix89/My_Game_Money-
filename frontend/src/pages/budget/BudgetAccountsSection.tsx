// src/pages/budget/BudgetAccountsSection.tsx
import React, { useEffect, useMemo, useState } from "react";
import {
    fetchAccounts,
    createAccount,
    updateAccount,
    deleteAccount,
    setMainForPayments,
} from "../../api/budgetAccountsApi";
import type { AccountDto, AccountType } from "../../types/budget/accounts";

import { fetchBanks } from "../../api/budgetBanksApi";
import type { BankDto } from "../../types/budget/banks";

import axiosClient from "../../api/axiosClient";

import "./budget-accounts.css";

type FormMode = "create" | "edit";

/**
 * UI-типы для пользователя:
 * - DEBIT   → дебетовая карта
 * - CREDIT  → кредитная карта (покупки / снятие)
 * - ASSET   → актив
 */
type UiAccountType = "DEBIT" | "CREDIT" | "ASSET";

type FormState = {
    bankName: string;
    name: string;
    uiType: UiAccountType;
    limit: number | null;
    balance: number;
    mainForPayments: boolean;
    forWithdraw: boolean;
};

const accountTypeLabels: Record<AccountType, string> = {
    DEBIT: "Дебетовая карта",
    CREDIT_PURCHASE: "Кредитная карта",
    CREDIT_CASH: "Кредитная карта (снятие)",
    ASSET: "Актив",
};

const emptyForm: FormState = {
    bankName: "",
    name: "",
    uiType: "DEBIT",
    limit: null,
    balance: 0,
    mainForPayments: false,
    forWithdraw: false,
};

type ApiError = {
    response?: {
        data?: {
            error?: string;
            code?: string;
        };
    };
};

const BudgetAccountsSection: React.FC = () => {
    const [accounts, setAccounts] = useState<AccountDto[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // 🔹 Банки (справочник)
    const [banks, setBanks] = useState<BankDto[]>([]);
    const [banksLoading, setBanksLoading] = useState(false);

    // выбранный банк в модалке
    const [selectedBankId, setSelectedBankId] = useState<number | null>(null);

    const [isModalOpen, setIsModalOpen] = useState(false);
    const [formMode, setFormMode] = useState<FormMode>("create");
    const [editingId, setEditingId] = useState<number | null>(null);
    const [form, setForm] = useState<FormState>(emptyForm);
    const [saving, setSaving] = useState(false);

    // режим создания нового банка в модалке
    const [isBankCreateMode, setIsBankCreateMode] = useState(false);
    const [newBankName, setNewBankName] = useState("");
    const [bankError, setBankError] = useState<string | null>(null);

    // ===========================
    //   Загрузка счетов и банков
    // ===========================
    useEffect(() => {
        const loadAccounts = async () => {
            try {
                setLoading(true);
                setError(null);
                const data = await fetchAccounts();
                setAccounts(data);
            } catch (e) {
                console.error(e);
                setError("Не удалось загрузить счета и карты");
            } finally {
                setLoading(false);
            }
        };

        void loadAccounts();
    }, []);

    useEffect(() => {
        const loadBanks = async () => {
            try {
                setBanksLoading(true);
                const data = await fetchBanks();
                setBanks(data);
            } catch (e) {
                console.error(e);
                setError("Не удалось загрузить справочник банков");
            } finally {
                setBanksLoading(false);
            }
        };

        void loadBanks();
    }, []);

    // ===========================
    //   Агрегаты для итогов
    // ===========================
    const totalAssets = useMemo(
        () =>
            accounts
                .filter((a) => a.type === "DEBIT" || a.type === "ASSET")
                .reduce((sum, a) => sum + a.balance, 0),
        [accounts]
    );

    // задолженность по кредиткам для снятия:
    // сумма (лимит − баланс) по картам forWithdraw && CREDIT_CASH
    const totalCreditCarousel = useMemo(
        () =>
            accounts
                .filter(
                    (a) =>
                        a.forWithdraw &&
                        a.type === "CREDIT_CASH" &&
                        a.limit != null
                )
                .reduce((sum, a) => sum + (a.limit! - a.balance), 0),
        [accounts]
    );

    const netBalance = totalAssets - totalCreditCarousel;

    const balancePositive = netBalance >= 0;
    const balanceBarWidth = (() => {
        if (totalAssets <= 0) return 10;
        const raw = Math.abs((netBalance / totalAssets) * 100);
        return Math.min(100, Math.max(10, raw));
    })();

    // ===========================
    //   Открытие / закрытие модалок
    // ===========================
    const openCreateModal = () => {
        setForm(emptyForm);
        setEditingId(null);
        setFormMode("create");
        setError(null);
        setSelectedBankId(null);
        setIsBankCreateMode(false);
        setNewBankName("");
        setBankError(null);
        setIsModalOpen(true);
    };

    const mapAccountToForm = (acc: AccountDto): FormState => {
        let uiType: UiAccountType = "DEBIT";
        if (acc.type === "ASSET") uiType = "ASSET";
        else if (acc.type === "DEBIT") uiType = "DEBIT";
        else uiType = "CREDIT";

        return {
            bankName: acc.bankName,
            name: acc.name,
            uiType,
            limit: acc.limit,
            balance: acc.balance,
            mainForPayments: acc.mainForPayments,
            forWithdraw: acc.forWithdraw,
        };
    };

    const openEditModal = (acc: AccountDto) => {
        setForm(mapAccountToForm(acc));
        setEditingId(acc.id);
        setFormMode("edit");
        setError(null);
        setIsBankCreateMode(false);
        setNewBankName("");
        setBankError(null);

        // попытаться найти банк по имени
        const bank = banks.find((b) => b.name === acc.bankName);
        setSelectedBankId(bank ? bank.id : null);

        setIsModalOpen(true);
    };

    const closeModal = () => {
        if (saving) return;
        setIsModalOpen(false);
    };

    // ===========================
    //   Обработчики полей формы
    // ===========================
    const handleInputChange =
        (field: keyof FormState) =>
            (
                e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
            ) => {
                const value = e.target.value;

                setForm((prev) => {
                    if (field === "balance") {
                        return {
                            ...prev,
                            balance: Number(value.replace(",", ".")) || 0,
                        };
                    }
                    if (field === "limit") {
                        const trimmed = value.trim();
                        return {
                            ...prev,
                            limit:
                                trimmed === ""
                                    ? null
                                    : Number(trimmed.replace(",", ".")) || 0,
                        };
                    }
                    if (field === "uiType") {
                        const uiType = value as UiAccountType;
                        // при смене типа сбрасываем лишние флажки / лимит
                        if (uiType === "DEBIT") {
                            return {
                                ...prev,
                                uiType,
                                mainForPayments: false,
                                forWithdraw: false,
                                limit: null,
                            };
                        }
                        if (uiType === "CREDIT") {
                            return {
                                ...prev,
                                uiType,
                                mainForPayments: false,
                            };
                        }
                        // ASSET
                        return {
                            ...prev,
                            uiType,
                            mainForPayments: false,
                            forWithdraw: false,
                            limit: null,
                        };
                    }
                    if (field === "bankName" || field === "name") {
                        return { ...prev, [field]: value };
                    }
                    return prev;
                });
            };

    const handleCheckboxChange =
        (field: "forWithdraw" | "mainForPayments") =>
            (e: React.ChangeEvent<HTMLInputElement>) => {
                const checked = e.target.checked;
                setForm((prev) => ({ ...prev, [field]: checked }));
            };

    // выбор банка из справочника
    const handleBankSelectChange = (
        e: React.ChangeEvent<HTMLSelectElement>
    ) => {
        const value = e.target.value;
        setBankError(null);
        if (!value) {
            setSelectedBankId(null);
            setForm((prev) => ({ ...prev, bankName: "" }));
            return;
        }
        const id = Number(value);
        setSelectedBankId(id);
        const bank = banks.find((b) => b.id === id);
        setForm((prev) => ({
            ...prev,
            bankName: bank ? bank.name : prev.bankName,
        }));
    };

    // создание банка через форму
    const handleCreateBankSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const trimmed = newBankName.trim();
        if (!trimmed) {
            setBankError("Введите название банка");
            return;
        }

        try {
            setBankError(null);
            const response = await axiosClient.post<BankDto>(
                "/api/budget/banks",
                { name: trimmed }
            );
            const created = response.data;
            setBanks((prev) => [...prev, created]);
            setSelectedBankId(created.id);
            setForm((prev) => ({ ...prev, bankName: created.name }));
            setIsBankCreateMode(false);
            setNewBankName("");
        } catch (e) {
            console.error(e);
            setBankError("Не удалось создать банк");
        }
    };

    const handleDeleteBankClick = async () => {
        if (!selectedBankId) {
            setBankError("Выберите банк для удаления");
            return;
        }
        const bank = banks.find((b) => b.id === selectedBankId);
        const bankName = bank?.name ?? "";

        if (
            !window.confirm(
                `При удалении банка "${bankName}" автоматически удалятся привязанные карты. Продолжить?`
            )
        ) {
            return;
        }

        try {
            setBankError(null);
            await axiosClient.delete(`/api/budget/banks/${selectedBankId}`);

            setBanks((prev) => prev.filter((b) => b.id !== selectedBankId));
            setAccounts((prev) =>
                prev.filter((a) => a.bankName !== bankName)
            );
            setSelectedBankId(null);
            setForm((prev) => ({ ...prev, bankName: "" }));
        } catch (e) {
            console.error(e);
            setBankError("Не удалось удалить банк");
        }
    };

    // ===========================
    //   Маппинг формы → backend-DTO
    // ===========================
    const buildBackendType = (formState: FormState): AccountType => {
        if (formState.uiType === "DEBIT") return "DEBIT";
        if (formState.uiType === "ASSET") return "ASSET";
        // CREDIT
        return formState.forWithdraw ? "CREDIT_CASH" : "CREDIT_PURCHASE";
    };

    const buildAccountPayload = (
        formState: FormState
    ): Omit<AccountDto, "id"> => {
        const backendType = buildBackendType(formState);

        return {
            bankName: formState.bankName.trim(),
            name: formState.name.trim(),
            type: backendType,
            limit:
                backendType === "CREDIT_CASH" ||
                backendType === "CREDIT_PURCHASE"
                    ? formState.limit
                    : null,
            balance: formState.balance,
            mainForPayments: backendType === "DEBIT"
                ? formState.mainForPayments
                : false,
            forWithdraw:
                backendType === "CREDIT_CASH" ? true : formState.forWithdraw,
        };
    };

    // ===========================
    //   Сохранение счёта
    // ===========================
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        const bankName = form.bankName.trim();
        const name = form.name.trim();

        if (!bankName) {
            setError("Укажите банк");
            return;
        }
        if (!name) {
            setError(
                form.uiType === "ASSET"
                    ? "Укажите название актива"
                    : "Укажите название счёта / карты"
            );
            return;
        }

        if (
            form.uiType === "CREDIT" &&
            (form.limit == null || form.limit <= 0)
        ) {
            setError("Укажите кредитный лимит для кредитной карты");
            return;
        }

        try {
            setSaving(true);
            const payload = buildAccountPayload(form);
            let saved: AccountDto;

            if (formMode === "create") {
                saved = await createAccount(payload);
                setAccounts((prev) => [...prev, saved]);
            } else if (editingId != null) {
                saved = await updateAccount(editingId, payload);
                setAccounts((prev) =>
                    prev.map((a) => (a.id === saved.id ? saved : a))
                );
            }

            setIsModalOpen(false);
        } catch (e: unknown) {
            console.error(e);
            const err = e as ApiError;
            if (
                err.response?.data?.error === "ACCOUNT_HAS_TRANSACTIONS" ||
                err.response?.data?.code === "ACCOUNT_HAS_TRANSACTIONS"
            ) {
                setError(
                    "Нельзя изменить этот счёт: к нему привязаны операции"
                );
            } else {
                setError("Не удалось сохранить счёт");
            }
        } finally {
            setSaving(false);
        }
    };

    // ===========================
    //   Удаление и "сделать основной"
    // ===========================
    const handleDelete = async (acc: AccountDto) => {
        if (
            !window.confirm(
                `Удалить счёт "${acc.bankName} — ${acc.name}"? Действие может быть необратимым.`
            )
        ) {
            return;
        }

        try {
            setError(null);
            await deleteAccount(acc.id);
            setAccounts((prev) => prev.filter((a) => a.id !== acc.id));
        } catch (e: unknown) {
            console.error(e);
            const err = e as ApiError;
            if (
                err.response?.data?.error === "ACCOUNT_HAS_TRANSACTIONS" ||
                err.response?.data?.code === "ACCOUNT_HAS_TRANSACTIONS"
            ) {
                setError("Нельзя удалить счёт: к нему привязаны операции");
            } else {
                setError("Не удалось удалить счёт");
            }
        }
    };

    const handleSetMain = async (acc: AccountDto) => {
        try {
            setError(null);
            const updated = await setMainForPayments(acc.id);
            setAccounts((prev) =>
                prev.map((a) => ({
                    ...a,
                    mainForPayments: a.id === updated.id,
                }))
            );
        } catch (e) {
            console.error(e);
            setError("Не удалось отметить счёт как основной для списаний");
        }
    };

    // ===========================
    //   Рендер
    // ===========================
    return (
        <section className="finance-panel finance-accounts">
            <div className="panel-header accounts-panel-header">
                <div>
                    <h2 className="panel-title">Счета и карты</h2>
                    <span className="panel-subtitle">
                        Дебетовые, кредитные и активы, включая основную карту.
                    </span>
                </div>
                <button
                    type="button"
                    className="btn btn-add-account"
                    onClick={openCreateModal}
                >
                    + Добавить счёт / карту
                </button>
            </div>

            {loading && <div className="info-row">Загрузка счетов...</div>}
            {error && <div className="error-row">{error}</div>}

            {/* Таблица счетов */}
            <div className="accounts-table">
                <div className="accounts-header-row">
                    <span>Банк</span>
                    <span>Счёт / карта / актив</span>
                    <span>Тип</span>
                    <span>Лимит</span>
                    <span>Баланс</span>
                    <span className="accounts-actions-col">Действия</span>
                </div>

                {accounts.length === 0 && !loading && (
                    <div className="accounts-row empty-row">
                        Счета пока не добавлены
                    </div>
                )}

                {accounts.map((acc) => (
                    <div key={acc.id} className="accounts-row">
                        <span>{acc.bankName}</span>
                        <span className="accounts-name-cell">
                            <span>{acc.name}</span>
                            {acc.mainForPayments && (
                                <span className="tag-primary">ОСНОВНАЯ</span>
                            )}
                            {acc.forWithdraw && (
                                <span className="tag-secondary">
                                    для снятия
                                </span>
                            )}
                        </span>
                        <span className="accounts-type">
                            {accountTypeLabels[acc.type]}
                        </span>
                        <span className="accounts-limit">
                            {acc.limit != null
                                ? `${acc.limit.toLocaleString("ru-RU")} ₽`
                                : "—"}
                        </span>
                        <span className="accounts-balance">
                            {acc.balance.toLocaleString("ru-RU")} ₽
                        </span>
                        <span className="accounts-actions-col">
                            <button
                                type="button"
                                className="btn btn-sm"
                                onClick={() => openEditModal(acc)}
                            >
                                Редактировать
                            </button>
                            <button
                                type="button"
                                className="btn btn-sm btn-main"
                                disabled={acc.mainForPayments}
                                onClick={() => handleSetMain(acc)}
                            >
                                {acc.mainForPayments
                                    ? "Основная"
                                    : "Сделать основной"}
                            </button>
                            <button
                                type="button"
                                className="btn btn-sm btn-danger"
                                onClick={() => handleDelete(acc)}
                            >
                                Удалить
                            </button>
                        </span>
                    </div>
                ))}
            </div>

            {/* Итоги с полосами */}
            <div className="accounts-summary">
                <h3 className="summary-title">Итоги</h3>

                <div className="summary-row">
                    <div className="summary-label">
                        Задолженность (кредитная карусель)
                    </div>
                    <div className="summary-value">
                        {totalCreditCarousel.toLocaleString("ru-RU")} ₽
                    </div>
                </div>
                <div className="summary-bar summary-bar-negative">
                    <div
                        className="summary-bar-fill"
                        style={{
                            width: `${Math.min(
                                100,
                                totalAssets > 0
                                    ? (totalCreditCarousel / totalAssets) * 100
                                    : 0
                            )}%`,
                        }}
                    />
                </div>

                <div className="summary-row">
                    <div className="summary-label">
                        Активы (дебет + активы)
                    </div>
                    <div className="summary-value">
                        {totalAssets.toLocaleString("ru-RU")} ₽
                    </div>
                </div>
                <div className="summary-bar summary-bar-positive">
                    <div className="summary-bar-fill" style={{ width: "100%" }} />
                </div>

                <div className="summary-row summary-row-highlight">
                    <div className="summary-label">Баланс</div>
                    <div className="summary-value">
                        {netBalance.toLocaleString("ru-RU")} ₽
                    </div>
                </div>
                <div
                    className={
                        "summary-bar " +
                        (balancePositive
                            ? "summary-bar-gold-positive"
                            : "summary-bar-gold-negative")
                    }
                >
                    <div
                        className="summary-bar-fill"
                        style={{ width: `${balanceBarWidth}%` }}
                    />
                </div>
            </div>

            {/* Модалка добавления / редактирования счёта */}
            {isModalOpen && (
                <div className="modal-backdrop" onClick={closeModal}>
                    <div
                        className="modal"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <h2 className="modal-title">
                            {formMode === "create"
                                ? "Добавить счёт / карту / актив"
                                : "Редактировать счёт / карту / актив"}
                        </h2>

                        <form className="modal-form" onSubmit={handleSubmit}>
                            {/* Банк — выбор из справочника + создание / удаление */}
                            <div className="modal-field">
                                <label>Банк</label>
                                <select
                                    value={selectedBankId ?? ""}
                                    onChange={handleBankSelectChange}
                                >
                                    <option value="">
                                        {banksLoading
                                            ? "Загрузка банков..."
                                            : "Выберите банк"}
                                    </option>
                                    {banks.map((b) => (
                                        <option key={b.id} value={b.id}>
                                            {b.name}
                                        </option>
                                    ))}
                                </select>

                                <div className="bank-actions-row">
                                    <button
                                        type="button"
                                        className="btn btn-sm"
                                        onClick={() => {
                                            setIsBankCreateMode((prev) => !prev);
                                            setBankError(null);
                                        }}
                                    >
                                        {isBankCreateMode
                                            ? "Отмена создания банка"
                                            : "+ Новый банк"}
                                    </button>
                                    <button
                                        type="button"
                                        className="btn btn-sm btn-danger"
                                        onClick={handleDeleteBankClick}
                                        disabled={!selectedBankId}
                                    >
                                        Удалить банк
                                    </button>
                                </div>

                                {isBankCreateMode && (
                                    <form
                                        className="bank-create-form"
                                        onSubmit={handleCreateBankSubmit}
                                    >
                                        <input
                                            type="text"
                                            placeholder="Название банка"
                                            value={newBankName}
                                            onChange={(e) =>
                                                setNewBankName(e.target.value)
                                            }
                                        />
                                        <button
                                            type="submit"
                                            className="btn btn-sm btn-main"
                                        >
                                            Сохранить банк
                                        </button>
                                    </form>
                                )}

                                {bankError && (
                                    <div className="error-row">{bankError}</div>
                                )}
                            </div>

                            {/* Название счёта / карты / актива */}
                            <div className="modal-field">
                                <label>
                                    {form.uiType === "ASSET"
                                        ? "Название актива"
                                        : "Счёт / карта"}
                                </label>
                                <input
                                    type="text"
                                    value={form.name}
                                    onChange={handleInputChange("name")}
                                    placeholder={
                                        form.uiType === "ASSET"
                                            ? "Например, Доллары, Золото"
                                            : "Например, Т-дебет основная *0014"
                                    }
                                />
                            </div>

                            {/* Тип */}
                            <div className="modal-field">
                                <label>Тип</label>
                                <select
                                    value={form.uiType}
                                    onChange={handleInputChange("uiType")}
                                >
                                    <option value="DEBIT">
                                        Дебетовая карта
                                    </option>
                                    <option value="CREDIT">
                                        Кредитная карта
                                    </option>
                                    <option value="ASSET">Актив</option>
                                </select>
                            </div>

                            {/* Кредитный лимит — только для кредиток */}
                            {form.uiType === "CREDIT" && (
                                <div className="modal-field">
                                    <label>Кредитный лимит</label>
                                    <input
                                        type="text"
                                        value={
                                            form.limit != null
                                                ? String(form.limit)
                                                : ""
                                        }
                                        onChange={handleInputChange("limit")}
                                        placeholder="Например, 150000"
                                    />
                                </div>
                            )}

                            {/* Баланс / стоимость — всегда */}
                            <div className="modal-field">
                                <label>Баланс / стоимость</label>
                                <input
                                    type="text"
                                    value={String(form.balance)}
                                    onChange={handleInputChange("balance")}
                                    placeholder="Например, 85000"
                                />
                            </div>

                            {/* Галочки — зависят от типа */}
                            {form.uiType === "CREDIT" && (
                                <div className="modal-field checkbox-row">
                                    <label className="checkbox-inline">
                                        <input
                                            type="checkbox"
                                            checked={form.forWithdraw}
                                            onChange={handleCheckboxChange(
                                                "forWithdraw"
                                            )}
                                        />
                                        <span>
                                            Использовать как карту для снятия
                                        </span>
                                    </label>
                                </div>
                            )}

                            {form.uiType === "DEBIT" && (
                                <div className="modal-field checkbox-row">
                                    <label className="checkbox-inline">
                                        <input
                                            type="checkbox"
                                            checked={form.mainForPayments}
                                            onChange={handleCheckboxChange(
                                                "mainForPayments"
                                            )}
                                        />
                                        <span>Сделать основной для списаний</span>
                                    </label>
                                </div>
                            )}

                            {error && (
                                <div className="error-row">{error}</div>
                            )}

                            <div className="modal-actions">
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={closeModal}
                                    disabled={saving}
                                >
                                    Отмена
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-add-account primary"
                                    disabled={saving}
                                >
                                    {saving ? "Сохранение..." : "Сохранить"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </section>
    );
};

export default BudgetAccountsSection;
