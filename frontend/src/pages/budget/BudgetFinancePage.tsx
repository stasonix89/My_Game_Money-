// src/pages/budget/BudgetFinancePage.tsx
import React, {
    useCallback,
    useEffect,
    useMemo,
    useState,
} from "react";
import "./budget-finance.css";
import BudgetAccountsSection from "./BudgetAccountsSection";

import {
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
} from "../../api/budgetCategoriesApi";
import type {
    CategoryDto,
    OperationKind,
} from "../../types/budget/categories";

import {
    fetchTransactions,
    createTransaction,
} from "../../api/budgetTransactionsApi";
import type { TransactionDto } from "../../types/budget/transactions";

import { fetchAccounts } from "../../api/budgetAccountsApi";
import type { AccountDto } from "../../types/budget/accounts";

import AddTransactionModal from "./AddTransactionModal";
import type { CreateTransactionPayload } from "./AddTransactionModal";

type OperationType = "INCOME" | "EXPENSE" | "TRANSFER";

type OperationRow = {
    id: number;
    date: string; // "YYYY-MM-DD"
    type: OperationType;
    category: string;
    accountFrom: string;
    accountTo?: string;
    amount: number;
};

type CategoryApiError = {
    response?: {
        data?: {
            error?: string;
            code?: string;
            message?: string;
        };
    };
};

const BudgetFinancePage: React.FC = () => {
    // 🔹 Выбранный месяц (формат "YYYY-MM")
    const [selectedMonth, setSelectedMonth] = useState<string>("2025-11");
    const [selectedType, setSelectedType] = useState<"" | OperationType>("");

    const [selectedCategory, setSelectedCategory] = useState<string>("");

    // 🔹 Категории из справочника
    const [categories, setCategories] = useState<CategoryDto[]>([]);
    const [categoriesLoading, setCategoriesLoading] = useState(false);
    const [categoriesError, setCategoriesError] = useState<string | null>(null);

    // 🔹 Операции из backend
    const [transactions, setTransactions] = useState<TransactionDto[]>([]);
    const [transactionsLoading, setTransactionsLoading] = useState(false);
    const [transactionsError, setTransactionsError] = useState<string | null>(
        null
    );

    // 🔹 Счета для модалки + отображения названий
    const [accounts, setAccounts] = useState<AccountDto[]>([]);
    const [accountsLoading, setAccountsLoading] = useState(false);
    const [accountsError, setAccountsError] = useState<string | null>(null);

    // 🔹 Модалка управления категориями
    const [isCategoriesModalOpen, setIsCategoriesModalOpen] = useState(false);
    const [categoryFormName, setCategoryFormName] = useState("");
    const [categoryModalTypeFilter, setCategoryModalTypeFilter] =
        useState<OperationKind>("EXPENSE");
    const [editingCategoryId, setEditingCategoryId] = useState<number | null>(
        null
    );
    const [categorySaving, setCategorySaving] = useState(false);
    const [categoryModalError, setCategoryModalError] = useState<string | null>(
        null
    );

    // 🔹 Модалка "Добавить операцию"
    const [isAddModalOpen, setIsAddModalOpen] = useState(false);

    // ==============================
    //   Загрузка категорий с backend
    // ==============================
    useEffect(() => {
        const loadCategories = async () => {
            try {
                setCategoriesLoading(true);
                setCategoriesError(null);
                const data = await fetchCategories();
                setCategories(data);
            } catch (e) {
                console.error(e);
                setCategoriesError("Не удалось загрузить категории");
            } finally {
                setCategoriesLoading(false);
            }
        };

        void loadCategories();
    }, []);

    // ==============================
    //   Загрузка счетов с backend
    // ==============================
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

    // ==============================
    //   Загрузка операций с backend
    // ==============================
    const reloadTransactions = useCallback(async () => {
        try {
            setTransactionsLoading(true);
            setTransactionsError(null);

            if (!selectedMonth) {
                setTransactions([]);
                return;
            }

            const [yearStr, monthStr] = selectedMonth.split("-");
            const year = Number(yearStr);
            const month = Number(monthStr);

            if (!year || !month) {
                setTransactions([]);
                return;
            }

            const data = await fetchTransactions({ year, month });
            setTransactions(data);
        } catch (e) {
            console.error(e);
            setTransactionsError("Не удалось загрузить операции");
        } finally {
            setTransactionsLoading(false);
        }
    }, [selectedMonth]);

    useEffect(() => {
        void reloadTransactions();
    }, [reloadTransactions]);

    // ==============================
    //   Маппинг счетов для удобства
    // ==============================
    const accountById = useMemo(() => {
        const map = new Map<number, AccountDto>();
        accounts.forEach((a) => map.set(a.id, a));
        return map;
    }, [accounts]);

    const formatAccount = (acc: AccountDto | undefined): string => {
        if (!acc) return "";
        return `${acc.bankName} — ${acc.name}`;
    };

    // ==============================
    //   Маппинг DTO → строки таблицы
    // ==============================
    const operations: OperationRow[] = useMemo(() => {
        return transactions.map((tx) => {
            const fromAcc = tx.fromAccountId
                ? accountById.get(tx.fromAccountId)
                : undefined;
            const toAcc = tx.toAccountId
                ? accountById.get(tx.toAccountId)
                : undefined;

            const signedAmount =
                tx.type === "EXPENSE" || tx.type === "TRANSFER"
                    ? -Math.abs(tx.amount)
                    : Math.abs(tx.amount);

            return {
                id: tx.id,
                date: tx.date,
                type: tx.type as OperationType,
                category: tx.category,
                accountFrom:
                    fromAcc != null
                        ? formatAccount(fromAcc)
                        : tx.fromAccountId != null
                            ? `Счёт #${tx.fromAccountId}`
                            : "",
                accountTo:
                    toAcc != null
                        ? formatAccount(toAcc)
                        : tx.toAccountId != null
                            ? `Счёт #${tx.toAccountId}`
                            : undefined,
                amount: signedAmount,
            };
        });
    }, [transactions, accountById]);

    // 🔹 fallback-категории из операций (если справочник пустой)
    const fallbackCategoriesFromOps = useMemo(() => {
        const set = new Set<string>();
        operations.forEach((op) => set.add(op.category));
        return Array.from(set);
    }, [operations]);

    // 🔹 Категории для фильтра (в хедере): по выбранному типу
    const categoriesForFilter = useMemo(() => {
        if (categories.length > 0) {
            const filtered = categories.filter((cat) =>
                selectedType ? cat.operationType === selectedType : true
            );
            const names = filtered.map((c) => c.name);
            return Array.from(new Set(names));
        }
        // если справочник ещё пустой — подхватываем из операций
        return fallbackCategoriesFromOps;
    }, [categories, selectedType, fallbackCategoriesFromOps]);

    // 🔹 Список категорий в модалке (по выбранному типу в модалке)
    const categoriesForModal = useMemo(
        () =>
            categories.filter(
                (c) => c.operationType === categoryModalTypeFilter
            ),
        [categories, categoryModalTypeFilter]
    );

    // ==============================
    //   Фильтрация операций
    // ==============================
    const filteredOperations = useMemo(() => {
        return operations.filter((op) => {
            // фильтр по дате (месяц/год)
            if (selectedMonth) {
                const [year, month] = selectedMonth.split("-");
                const opYear = op.date.slice(0, 4);
                const opMonth = op.date.slice(5, 7);
                if (opYear !== year || opMonth !== month) {
                    return false;
                }
            }

            // фильтр по типу
            if (selectedType && op.type !== selectedType) {
                return false;
            }

            // фильтр по категории
            if (selectedCategory && op.category !== selectedCategory) {
                return false;
            }

            return true;
        });
    }, [operations, selectedMonth, selectedType, selectedCategory]);

    // ==============================
    //   Модалка "Управление категориями"
    // ==============================
    const openCategoriesModal = () => {
        setCategoryModalError(null);
        // стартовый тип — выбранный в фильтре, либо "EXPENSE"
        const startType: OperationKind =
            (selectedType as OperationKind) || "EXPENSE";
        setCategoryModalTypeFilter(startType);
        setCategoryFormName("");
        setEditingCategoryId(null);
        setIsCategoriesModalOpen(true);
    };

    const closeCategoriesModal = () => {
        if (categorySaving) return;
        setIsCategoriesModalOpen(false);
    };

    const handleCategoryEditClick = (cat: CategoryDto) => {
        setEditingCategoryId(cat.id);
        setCategoryFormName(cat.name);
        setCategoryModalTypeFilter(cat.operationType);
        setCategoryModalError(null);
    };

    const resetCategoryForm = () => {
        setEditingCategoryId(null);
        setCategoryFormName("");
        setCategoryModalError(null);
    };

    const handleCategorySubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const name = categoryFormName.trim();
        if (!name) {
            setCategoryModalError("Введите название категории");
            return;
        }

        try {
            setCategorySaving(true);
            setCategoryModalError(null);

            if (editingCategoryId == null) {
                // создание
                const created = await createCategory({
                    name,
                    operationType: categoryModalTypeFilter,
                });
                setCategories((prev) => [...prev, created]);
            } else {
                // обновление
                const updated = await updateCategory(editingCategoryId, {
                    name,
                    operationType: categoryModalTypeFilter,
                });
                setCategories((prev) =>
                    prev.map((c) => (c.id === updated.id ? updated : c))
                );
            }

            resetCategoryForm();
        } catch (e: unknown) {
            console.error(e);
            const err = e as CategoryApiError;
            const backendMessage =
                err.response?.data?.message ||
                err.response?.data?.error ||
                err.response?.data?.code;
            setCategoryModalError(
                backendMessage || "Не удалось сохранить категорию"
            );
        } finally {
            setCategorySaving(false);
        }
    };

    const handleCategoryDelete = async (cat: CategoryDto) => {
        if (
            !window.confirm(
                `Удалить категорию "${cat.name}" для типа "${cat.operationType}"?`
            )
        ) {
            return;
        }

        try {
            setCategoryModalError(null);
            await deleteCategory(cat.id);
            setCategories((prev) => prev.filter((c) => c.id !== cat.id));
            // если удалили редактируемую — сбрасываем форму
            if (editingCategoryId === cat.id) {
                resetCategoryForm();
            }
        } catch (e: unknown) {
            console.error(e);
            const err = e as CategoryApiError;
            const backendMessage =
                err.response?.data?.message ||
                err.response?.data?.error ||
                err.response?.data?.code;
            setCategoryModalError(
                backendMessage || "Не удалось удалить категорию"
            );
        }
    };

    // ==============================
    //   Модалка "Добавить операцию"
    // ==============================
    const handleAddOperationClick = () => {
        setIsAddModalOpen(true);
    };

    const handleAddOperationSubmit = async (
        payload: CreateTransactionPayload
    ) => {
        await createTransaction({
            date: payload.date,
            type: payload.type,
            category: payload.category,
            fromAccountId: payload.accountFromId,
            toAccountId: payload.accountToId,
            amount: payload.amount,
        });

        await reloadTransactions();
    };

    // ==============================
    //   Разбор selectedMonth для модалки операций
    // ==============================
    const now = new Date();
    let filterYear = now.getFullYear();
    let filterMonth = now.getMonth() + 1;
    if (selectedMonth) {
        const [yStr, mStr] = selectedMonth.split("-");
        const y = Number(yStr);
        const m = Number(mStr);
        if (y && m) {
            filterYear = y;
            filterMonth = m;
        }
    }

    // ==============================
    //   Рендер
    // ==============================
    return (
        <div className="page-container budget-finance">
            {/* Заголовок + фильтры + кнопка "+" */}
            <div className="finance-header">
                <div>
                    <h1 className="gold-title gold-title-line page-title">
                        Учёт финансов
                    </h1>
                    <p className="page-description">
                        Транзакции, счета, кредитные лимиты и активы за выбранный
                        период.
                    </p>
                </div>

                <div className="finance-header-controls">
                    <div className="finance-filters">
                        <div className="filter-group">
                            <span className="filter-label">Дата</span>
                            <input
                                type="month"
                                className="filter-control"
                                value={selectedMonth}
                                onChange={(e) =>
                                    setSelectedMonth(e.target.value)
                                }
                            />
                        </div>

                        <div className="filter-group">
                            <span className="filter-label">Тип операции</span>
                            <select
                                className="filter-control"
                                value={selectedType}
                                onChange={(e) =>
                                    setSelectedType(
                                        (e.target.value ||
                                            "") as "" | OperationType
                                    )
                                }
                            >
                                <option value="">Все</option>
                                <option value="INCOME">Доход</option>
                                <option value="EXPENSE">Расход</option>
                                <option value="TRANSFER">Перевод</option>
                            </select>
                        </div>

                        <div className="filter-group">
                            <span className="filter-label">
                                Категория{" "}
                                {categoriesLoading
                                    ? "(загрузка...)"
                                    : categoriesError
                                        ? "(ошибка)"
                                        : ""}
                            </span>
                            <div style={{ display: "flex", gap: 6 }}>
                                <select
                                    className="filter-control"
                                    value={selectedCategory}
                                    onChange={(e) =>
                                        setSelectedCategory(e.target.value)
                                    }
                                >
                                    <option value="">Все</option>
                                    {categoriesForFilter.map((cat) => (
                                        <option key={cat} value={cat}>
                                            {cat}
                                        </option>
                                    ))}
                                </select>
                                <button
                                    type="button"
                                    className="btn btn-sm"
                                    onClick={openCategoriesModal}
                                >
                                    ⋯
                                </button>
                            </div>
                        </div>
                    </div>

                    <button
                        className="add-op-button"
                        type="button"
                        onClick={handleAddOperationClick}
                    >
                        <span className="add-op-plus">＋</span>
                        <span>Добавить операцию</span>
                    </button>
                </div>
            </div>

            {/* Две колонки: слева операции, справа счета + итоги */}
            <div className="finance-grid">
                {/* Левая колонка — таблица операций */}
                <section className="finance-panel finance-operations">
                    <div className="panel-header">
                        <h2 className="panel-title">Операции</h2>
                        <span className="panel-subtitle">
                            {transactionsLoading
                                ? "Загружаем операции..."
                                : transactionsError
                                    ? "Не удалось загрузить операции"
                                    : "Операции за выбранный период."}
                        </span>
                    </div>

                    <div className="operations-table">
                        <div className="operations-header-row">
                            <span>Дата</span>
                            <span>Тип</span>
                            <span>Категория</span>
                            <span>Счёт</span>
                            <span>Сумма</span>
                        </div>

                        {filteredOperations.map((op) => (
                            <div
                                key={op.id}
                                className={`operations-row op-${op.type.toLowerCase()}`}
                            >
                                <span>{op.date}</span>
                                <span>
                                    {op.type === "INCOME"
                                        ? "Доход"
                                        : op.type === "EXPENSE"
                                            ? "Расход"
                                            : "Перевод"}
                                </span>
                                <span>{op.category}</span>
                                <span>
                                    {op.type === "TRANSFER" && op.accountTo
                                        ? `${op.accountFrom} → ${op.accountTo}`
                                        : op.accountFrom}
                                </span>
                                <span className="operations-amount">
                                    {op.amount > 0 ? "+" : ""}
                                    {op.amount.toLocaleString("ru-RU")} ₽
                                </span>
                            </div>
                        ))}

                        {filteredOperations.length === 0 &&
                            !transactionsLoading && (
                                <div className="operations-row empty-ops-row">
                                    Нет операций по выбранным фильтрам
                                </div>
                            )}
                    </div>
                </section>

                {/* Правая колонка — живой блок "Счета и карты" с редактированием */}
                <BudgetAccountsSection />
            </div>

            {/* Модалка управления категориями */}
            {isCategoriesModalOpen && (
                <div
                    className="modal-backdrop"
                    onClick={closeCategoriesModal}
                >
                    <div
                        className="modal"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <h2 className="modal-title">
                            Управление категориями
                        </h2>

                        <div className="modal-field">
                            <label>Тип операции</label>
                            <select
                                value={categoryModalTypeFilter}
                                onChange={(e) => {
                                    const value =
                                        e.target.value as OperationKind;
                                    setCategoryModalTypeFilter(value);
                                }}
                            >
                                <option value="INCOME">Доход</option>
                                <option value="EXPENSE">Расход</option>
                                <option value="TRANSFER">Перевод</option>
                            </select>
                        </div>

                        <div className="categories-list">
                            {categoriesForModal.length === 0 ? (
                                <div className="info-row">
                                    Для этого типа ещё нет категорий
                                </div>
                            ) : (
                                categoriesForModal.map((cat) => (
                                    <div
                                        key={cat.id}
                                        className="category-row"
                                    >
                                        <span>{cat.name}</span>
                                        <span className="category-actions">
                                            <button
                                                type="button"
                                                className="btn btn-sm"
                                                onClick={() =>
                                                    handleCategoryEditClick(
                                                        cat
                                                    )
                                                }
                                            >
                                                Редактировать
                                            </button>
                                            <button
                                                type="button"
                                                className="btn btn-sm btn-danger"
                                                onClick={() =>
                                                    handleCategoryDelete(cat)
                                                }
                                            >
                                                Удалить
                                            </button>
                                        </span>
                                    </div>
                                ))
                            )}
                        </div>

                        <form
                            className="modal-form"
                            onSubmit={handleCategorySubmit}
                            style={{ marginTop: 12 }}
                        >
                            <div className="modal-field">
                                <label>
                                    {editingCategoryId == null
                                        ? "Новая категория"
                                        : "Редактировать категорию"}
                                </label>
                                <input
                                    type="text"
                                    value={categoryFormName}
                                    onChange={(e) =>
                                        setCategoryFormName(e.target.value)
                                    }
                                    placeholder="Например, Продукты"
                                />
                            </div>

                            {categoryModalError && (
                                <div className="error-row">
                                    {categoryModalError}
                                </div>
                            )}

                            <div className="modal-actions">
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={resetCategoryForm}
                                    disabled={categorySaving}
                                >
                                    Очистить
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-add-account primary"
                                    disabled={categorySaving}
                                >
                                    {categorySaving
                                        ? "Сохранение..."
                                        : editingCategoryId == null
                                            ? "Добавить категорию"
                                            : "Сохранить изменения"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Статусы загрузки счетов для модалки операций */}
            {accountsLoading && (
                <div className="info-row">
                    Загрузка счетов для создания операций...
                </div>
            )}
            {accountsError && (
                <div className="error-row">{accountsError}</div>
            )}

            {/* Модалка "Добавить операцию" */}
            {isAddModalOpen && (
                <AddTransactionModal
                    isOpen={isAddModalOpen}
                    onClose={() => setIsAddModalOpen(false)}
                    year={filterYear}
                    month={filterMonth}
                    accounts={accounts}
                    categories={categories}
                    onSubmit={handleAddOperationSubmit}
                />
            )}
        </div>
    );
};

export default BudgetFinancePage;
