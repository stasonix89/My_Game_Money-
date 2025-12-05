// src/pages/personal/PersonalTasksPage.tsx
import React, { useEffect, useMemo, useState } from "react";
import "./personal-tasks.css";

import {
    personalTaskTypesApi,
} from "../../api/personalTaskTypesApi";
import {
    personalTasksApi,
} from "../../api/personalTasksApi";

import type {
    TaskDto,
    TaskTypeDto,
    TaskRequest,
} from "../../types/personal/tasks";

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

type ModalMode = "create" | "edit";

const getCurrentYearMonth = () => {
    const now = new Date();
    return {
        year: now.getFullYear(),
        month: now.getMonth() + 1,
    };
};

const pad2 = (n: number): string => (n < 10 ? `0${n}` : String(n));

const PersonalTasksPage: React.FC = () => {
    const { year: currentYear, month: currentMonth } = getCurrentYearMonth();

    // 🔹 Фильтры периода
    const [selectedYear, setSelectedYear] = useState<number>(currentYear);
    const [selectedMonth, setSelectedMonth] = useState<number>(currentMonth);

    // 🔹 Данные с backend
    const [taskTypes, setTaskTypes] = useState<TaskTypeDto[]>([]);
    const [tasks, setTasks] = useState<TaskDto[]>([]);

    const [loadingTypes, setLoadingTypes] = useState(false);
    const [loadingTasks, setLoadingTasks] = useState(false);
    const [errorTypes, setErrorTypes] = useState<string | null>(null);
    const [errorTasks, setErrorTasks] = useState<string | null>(null);

    // 🔹 Модалка задачи
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [modalMode, setModalMode] = useState<ModalMode>("create");
    const [editingTask, setEditingTask] = useState<TaskDto | null>(null);
    const [modalTypeName, setModalTypeName] = useState<string>("");
    const [modalText, setModalText] = useState<string>("");
    const [modalCompleted, setModalCompleted] = useState<boolean>(false);

    const [useExtendedDate, setUseExtendedDate] = useState<boolean>(false);
    const [modalDay, setModalDay] = useState<string>("");
    const [modalDateInput, setModalDateInput] = useState<string>("");

    const [modalSaving, setModalSaving] = useState<boolean>(false);
    const [modalError, setModalError] = useState<string | null>(null);

    // 🔹 Управление типами задач (внутри модалки)
    const [newTypeName, setNewTypeName] = useState<string>("");
    const [typesSaving, setTypesSaving] = useState<boolean>(false);
    const [typesManagerError, setTypesManagerError] = useState<string | null>(null);

    // ==========================
    //   Загрузка типов задач
    // ==========================
    useEffect(() => {
        const loadTypes = async () => {
            try {
                setLoadingTypes(true);
                setErrorTypes(null);
                const data = await personalTaskTypesApi.getTaskTypes();
                setTaskTypes(data);
            } catch {
                setErrorTypes("Не удалось загрузить типы задач.");
            } finally {
                setLoadingTypes(false);
            }
        };

        void loadTypes();
    }, []);

    // ==========================
    //   Загрузка задач
    // ==========================
    useEffect(() => {
        const loadTasks = async () => {
            try {
                setLoadingTasks(true);
                setErrorTasks(null);
                const data = await personalTasksApi.getTasks();
                setTasks(data);
            } catch {
                setErrorTasks("Не удалось загрузить список задач.");
            } finally {
                setLoadingTasks(false);
            }
        };

        void loadTasks();
    }, []);

    // ==========================
    //   Фильтрация задач
    // ==========================
    const currentTasks = useMemo(
        () =>
            tasks
                .filter(
                    (t) =>
                        t.year === selectedYear &&
                        t.month === selectedMonth &&
                        !t.completed
                )
                .sort((a, b) => a.date.localeCompare(b.date)),
        [tasks, selectedYear, selectedMonth]
    );

    const historyByYear = useMemo(() => {
        const map = new Map<number, TaskDto[]>();
        tasks
            .filter((t) => t.completed)
            .forEach((t) => {
                const arr = map.get(t.year) ?? [];
                arr.push(t);
                map.set(t.year, arr);
            });

        const result = Array.from(map.entries())
            .sort((a, b) => b[0] - a[0])
            .map(([year, items]) => ({
                year,
                items: items.sort((a, b) =>
                    a.date.localeCompare(b.date)
                ),
            }));

        return result;
    }, [tasks]);

    // ==========================
    //   Открытие / закрытие модалки
    // ==========================
    const resetModal = () => {
        setEditingTask(null);
        setModalTypeName(taskTypes[0]?.name ?? "");
        setModalText("");
        setModalCompleted(false);
        setUseExtendedDate(false);
        setModalDay("");
        setModalDateInput("");
        setModalError(null);
        setNewTypeName("");
        setTypesManagerError(null);
    };

    const openCreateModal = () => {
        setModalMode("create");
        resetModal();
        setIsModalOpen(true);
    };

    const openEditModal = (task: TaskDto) => {
        setModalMode("edit");
        setEditingTask(task);
        setModalTypeName(task.taskType);
        setModalText(task.text);
        setModalCompleted(task.completed);

        // По умолчанию при редактировании включаем расширенный режим
        setUseExtendedDate(true);
        setModalDateInput(task.date);
        setModalDay(""); // в этом режиме не используется

        setModalError(null);
        setNewTypeName("");
        setTypesManagerError(null);
        setIsModalOpen(true);
    };

    const closeModal = () => {
        if (modalSaving || typesSaving) return;
        setIsModalOpen(false);
    };

    // ==========================
    //   Управление типами задач
    // ==========================
    const handleCreateType = async () => {
        const name = newTypeName.trim();
        if (!name) {
            setTypesManagerError("Введите название типа задачи.");
            return;
        }

        try {
            setTypesSaving(true);
            setTypesManagerError(null);
            const created = await personalTaskTypesApi.createTaskType(name);
            setTaskTypes((prev) => [...prev, created]);
            setModalTypeName(created.name);
            setNewTypeName("");
        } catch {
            setTypesManagerError("Не удалось создать тип задачи.");
        } finally {
            setTypesSaving(false);
        }
    };

    const handleDeleteType = async (type: TaskTypeDto) => {
        if (
            !window.confirm(
                `Удалить тип задачи "${type.name}"? Задачи с этим типом останутся, но тип будет недоступен для выбора.`
            )
        ) {
            return;
        }

        try {
            setTypesSaving(true);
            setTypesManagerError(null);
            await personalTaskTypesApi.deleteTaskType(type.id);
            setTaskTypes((prev) => prev.filter((t) => t.id !== type.id));

            if (modalTypeName === type.name) {
                setModalTypeName(taskTypes.find((t) => t.id !== type.id)?.name ?? "");
            }
        } catch {
            setTypesManagerError("Не удалось удалить тип задачи.");
        } finally {
            setTypesSaving(false);
        }
    };

    // ==========================
    //   Построение payload для сохранения
    // ==========================
    const buildTaskRequest = (): TaskRequest | null => {
        const typeName = modalTypeName.trim();
        const text = modalText.trim();

        if (!typeName) {
            setModalError("Выбери тип задачи или создай новый.");
            return null;
        }
        if (!text) {
            setModalError("Введите описание задачи.");
            return null;
        }

        let year = selectedYear;
        let month = selectedMonth;
        let dateStr = "";

        if (useExtendedDate) {
            if (!modalDateInput) {
                setModalError("Укажи дату задачи.");
                return null;
            }
            dateStr = modalDateInput;
            const [yStr, mStr] = modalDateInput.split("-");
            const y = Number(yStr);
            const m = Number(mStr);
            if (!y || !m) {
                setModalError("Некорректная дата.");
                return null;
            }
            year = y;
            month = m;
        } else {
            const dayNum = Number(modalDay);
            if (!modalDay || Number.isNaN(dayNum) || dayNum < 1 || dayNum > 31) {
                setModalError("Укажи корректный день месяца (1–31).");
                return null;
            }
            dateStr = `${year}-${pad2(month)}-${pad2(dayNum)}`;
        }

        return {
            taskType: typeName,
            text,
            completed: modalCompleted,
            year,
            month,
            date: dateStr,
        };
    };

    // ==========================
    //   Сохранение задачи (create / edit)
    // ==========================
    const handleModalSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setModalError(null);

        const payload = buildTaskRequest();
        if (!payload) return;

        try {
            setModalSaving(true);

            if (modalMode === "create") {
                const created = await personalTasksApi.createTask(payload);
                setTasks((prev) => [...prev, created]);
            } else if (modalMode === "edit" && editingTask) {
                const updated = await personalTasksApi.updateTask(
                    editingTask.id,
                    payload
                );
                setTasks((prev) =>
                    prev.map((t) => (t.id === updated.id ? updated : t))
                );
            }

            setIsModalOpen(false);
        } catch {
            setModalError("Не удалось сохранить задачу. Попробуй ещё раз.");
        } finally {
            setModalSaving(false);
        }
    };

    // ==========================
    //   Операции над задачами из списков
    // ==========================
    const handleToggleCompleted = async (task: TaskDto) => {
        const payload: TaskRequest = {
            taskType: task.taskType,
            text: task.text,
            completed: !task.completed,
            year: task.year,
            month: task.month,
            date: task.date,
        };

        try {
            const updated = await personalTasksApi.updateTask(task.id, payload);
            setTasks((prev) =>
                prev.map((t) => (t.id === updated.id ? updated : t))
            );
        } catch {
            window.alert("Не удалось изменить статус задачи.");
        }
    };

    const handleDeleteTask = async (task: TaskDto) => {
        if (
            !window.confirm(
                `Удалить задачу "${task.text}"?`
            )
        ) {
            return;
        }

        try {
            await personalTasksApi.deleteTask(task.id);
            setTasks((prev) => prev.filter((t) => t.id !== task.id));
        } catch {
            window.alert("Не удалось удалить задачу.");
        }
    };

    // ==========================
    //   Рендер
    // ==========================
    return (
        <div className="page-container personal-tasks-page">
            {/* Заголовок и фильтры периода */}
            <div className="personal-tasks-header">
                <div>
                    <h1 className="gold-title gold-title-line page-title">
                        Личное — задачи
                    </h1>
                    <p className="page-description">
                        Планируй спорт, работу, здоровье и личные дела. Активные
                        задачи за выбранный месяц сверху, история — по годам ниже.
                    </p>
                </div>

                <div className="personal-tasks-filters">
                    <div className="filter-group">
                        <span className="filter-label">Месяц</span>
                        <select
                            className="filter-control"
                            value={selectedMonth}
                            onChange={(e) =>
                                setSelectedMonth(Number(e.target.value))
                            }
                        >
                            {MONTHS_RU.map((name, index) => (
                                <option key={name} value={index + 1}>
                                    {name}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="filter-group">
                        <span className="filter-label">Год</span>
                        <input
                            className="filter-control"
                            type="number"
                            value={selectedYear}
                            min={2000}
                            max={2100}
                            onChange={(e) =>
                                setSelectedYear(Number(e.target.value))
                            }
                        />
                    </div>

                    <button
                        type="button"
                        className="btn btn-add-task"
                        onClick={openCreateModal}
                    >
                        + Добавить задачу
                    </button>
                </div>
            </div>

            {/* Ошибки загрузки */}
            {errorTypes && (
                <div className="error-row">{errorTypes}</div>
            )}
            {errorTasks && (
                <div className="error-row">{errorTasks}</div>
            )}

            {/* Текущие задачи */}
            <section className="personal-tasks-section">
                <h2 className="section-title">Задачи за выбранный месяц</h2>

                {loadingTasks ? (
                    <div className="info-row">Загружаем задачи…</div>
                ) : currentTasks.length === 0 ? (
                    <div className="info-row">
                        Для выбранного месяца пока нет задач.
                    </div>
                ) : (
                    <div className="tasks-table">
                        <div className="tasks-header-row">
                            <span>Задача</span>
                            <span>Тип</span>
                            <span>Дата</span>
                            <span>Статус</span>
                            <span className="tasks-actions-col">Действия</span>
                        </div>

                        {currentTasks.map((task) => (
                            <div key={task.id} className="tasks-row">
                                <span className="tasks-text">{task.text}</span>
                                <span className="tasks-type">{task.taskType}</span>
                                <span>
                                    {new Date(task.date).toLocaleDateString("ru-RU")}
                                </span>
                                <span className="tasks-status">
                                    <label className="checkbox-inline">
                                        <input
                                            type="checkbox"
                                            checked={task.completed}
                                            onChange={() =>
                                                handleToggleCompleted(task)
                                            }
                                        />
                                        <span>
                                            {task.completed
                                                ? "Выполнено"
                                                : "Не выполнено"}
                                        </span>
                                    </label>
                                </span>
                                <span className="tasks-actions-col">
                                    <button
                                        type="button"
                                        className="btn btn-sm"
                                        onClick={() => openEditModal(task)}
                                    >
                                        Редактировать
                                    </button>
                                    <button
                                        type="button"
                                        className="btn btn-sm btn-danger"
                                        onClick={() => handleDeleteTask(task)}
                                    >
                                        Удалить
                                    </button>
                                </span>
                            </div>
                        ))}
                    </div>
                )}
            </section>

            {/* История задач */}
            <section className="personal-tasks-section personal-tasks-history">
                <h2 className="section-title">История задач (по годам)</h2>

                {historyByYear.length === 0 ? (
                    <div className="info-row">
                        История пока пустая — завершённые задачи появятся здесь.
                    </div>
                ) : (
                    historyByYear.map((block) => (
                        <div key={block.year} className="history-year-block">
                            <h3 className="history-year-title">
                                {block.year}
                            </h3>
                            <div className="tasks-table tasks-table--history">
                                <div className="tasks-header-row">
                                    <span>Задача</span>
                                    <span>Тип</span>
                                    <span>Дата</span>
                                    <span>Статус</span>
                                    <span className="tasks-actions-col">
                                        Действия
                                    </span>
                                </div>

                                {block.items.map((task) => (
                                    <div
                                        key={task.id}
                                        className="tasks-row tasks-row--history"
                                    >
                                        <span className="tasks-text">
                                            {task.text}
                                        </span>
                                        <span className="tasks-type">
                                            {task.taskType}
                                        </span>
                                        <span>
                                            {new Date(
                                                task.date
                                            ).toLocaleDateString("ru-RU")}
                                        </span>
                                        <span className="tasks-status">
                                            <label className="checkbox-inline">
                                                <input
                                                    type="checkbox"
                                                    checked={task.completed}
                                                    onChange={() =>
                                                        handleToggleCompleted(
                                                            task
                                                        )
                                                    }
                                                />
                                                <span>
                                                    {task.completed
                                                        ? "Выполнено"
                                                        : "Не выполнено"}
                                                </span>
                                            </label>
                                        </span>
                                        <span className="tasks-actions-col">
                                            <button
                                                type="button"
                                                className="btn btn-sm"
                                                onClick={() =>
                                                    openEditModal(task)
                                                }
                                            >
                                                Редактировать
                                            </button>
                                            <button
                                                type="button"
                                                className="btn btn-sm btn-danger"
                                                onClick={() =>
                                                    handleDeleteTask(task)
                                                }
                                            >
                                                Удалить
                                            </button>
                                        </span>
                                    </div>
                                ))}
                            </div>
                        </div>
                    ))
                )}
            </section>

            {/* Модалка создания / редактирования задачи */}
            {isModalOpen && (
                <div className="modal-backdrop" onClick={closeModal}>
                    <div
                        className="modal"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <h2 className="modal-title">
                            {modalMode === "create"
                                ? "Добавить задачу"
                                : "Редактировать задачу"}
                        </h2>

                        <form className="modal-form" onSubmit={handleModalSubmit}>
                            {/* Тип задачи */}
                            <div className="modal-field">
                                <label>Тип задачи</label>
                                <div className="modal-inline">
                                    <select
                                        className="field-select"
                                        value={modalTypeName}
                                        onChange={(e) =>
                                            setModalTypeName(e.target.value)
                                        }
                                    >
                                        <option value="">
                                            {loadingTypes
                                                ? "Загрузка типов…"
                                                : "Выбери тип"}
                                        </option>
                                        {taskTypes.map((t) => (
                                            <option key={t.id} value={t.name}>
                                                {t.name}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="task-types-manager">
                                    <div className="task-types-manager-header">
                                        <span className="field-label">
                                            Управление типами
                                        </span>
                                    </div>
                                    <div className="task-types-manager-body">
                                        <div className="task-types-new-row">
                                            <input
                                                type="text"
                                                className="field-input"
                                                placeholder="Новый тип (например, Спорт)"
                                                value={newTypeName}
                                                onChange={(e) =>
                                                    setNewTypeName(
                                                        e.target.value
                                                    )
                                                }
                                            />
                                            <button
                                                type="button"
                                                className="btn btn-sm"
                                                onClick={handleCreateType}
                                                disabled={typesSaving}
                                            >
                                                {typesSaving
                                                    ? "Добавляем…"
                                                    : "Добавить тип"}
                                            </button>
                                        </div>

                                        {taskTypes.length > 0 && (
                                            <div className="task-types-list">
                                                {taskTypes.map((t) => (
                                                    <div
                                                        key={t.id}
                                                        className="task-types-row"
                                                    >
                                                        <span>{t.name}</span>
                                                        <button
                                                            type="button"
                                                            className="btn btn-sm btn-danger"
                                                            onClick={() =>
                                                                handleDeleteType(
                                                                    t
                                                                )
                                                            }
                                                            disabled={
                                                                typesSaving
                                                            }
                                                        >
                                                            Удалить
                                                        </button>
                                                    </div>
                                                ))}
                                            </div>
                                        )}
                                        {typesManagerError && (
                                            <div className="error-row">
                                                {typesManagerError}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>

                            {/* Описание задачи */}
                            <div className="modal-field">
                                <label>Описание задачи</label>
                                <textarea
                                    className="field-textarea"
                                    value={modalText}
                                    onChange={(e) =>
                                        setModalText(e.target.value)
                                    }
                                    placeholder="Например: Пробежка 5 км"
                                    rows={3}
                                />
                            </div>

                            {/* Дата задачи: 2 режима */}
                            <div className="modal-field">
                                <label>Дата задачи</label>

                                <div className="date-mode-toggle">
                                    <label className="checkbox-inline">
                                        <input
                                            type="checkbox"
                                            checked={useExtendedDate}
                                            onChange={(e) =>
                                                setUseExtendedDate(
                                                    e.target.checked
                                                )
                                            }
                                        />
                                        <span>Расширенный выбор</span>
                                    </label>
                                </div>

                                {!useExtendedDate && (
                                    <div className="date-simple-mode">
                                        <div className="info-row">
                                            Месяц и год берутся из фильтров
                                            страницы:
                                            <strong>
                                                {" "}
                                                {MONTHS_RU[selectedMonth - 1]}{" "}
                                                {selectedYear}
                                            </strong>
                                            .
                                        </div>
                                        <div className="field-group">
                                            <label className="field-label">
                                                День месяца (1–31)
                                            </label>
                                            <input
                                                className="field-input"
                                                type="number"
                                                min={1}
                                                max={31}
                                                value={modalDay}
                                                onChange={(e) =>
                                                    setModalDay(
                                                        e.target.value
                                                    )
                                                }
                                            />
                                        </div>
                                    </div>
                                )}

                                {useExtendedDate && (
                                    <div className="date-extended-mode">
                                        <div className="field-group">
                                            <label className="field-label">
                                                Дата (полный календарь)
                                            </label>
                                            <input
                                                className="field-input"
                                                type="date"
                                                value={modalDateInput}
                                                onChange={(e) =>
                                                    setModalDateInput(
                                                        e.target.value
                                                    )
                                                }
                                            />
                                        </div>
                                    </div>
                                )}
                            </div>

                            {/* Статус */}
                            <div className="modal-field">
                                <label>Статус</label>
                                <label className="checkbox-inline">
                                    <input
                                        type="checkbox"
                                        checked={modalCompleted}
                                        onChange={(e) =>
                                            setModalCompleted(
                                                e.target.checked
                                            )
                                        }
                                    />
                                    <span>Выполнено</span>
                                </label>
                            </div>

                            {modalError && (
                                <div className="error-row">{modalError}</div>
                            )}

                            <div className="modal-actions">
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={closeModal}
                                    disabled={modalSaving || typesSaving}
                                >
                                    Отмена
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={modalSaving || typesSaving}
                                >
                                    {modalSaving
                                        ? "Сохраняем…"
                                        : "Сохранить задачу"}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PersonalTasksPage;
