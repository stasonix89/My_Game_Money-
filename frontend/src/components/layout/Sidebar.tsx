// src/components/layout/Sidebar.tsx
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./sidebar.css";

type NavItem = {
    label: string;
    path: string;
};

type NavSection = {
    title: string;
    items: NavItem[];
};

const sections: NavSection[] = [
    {
        title: "Бюджет",
        items: [
            { label: "Общий дашборд", path: "/budget/dashboard" },
            { label: "Учёт финансов", path: "/budget/finance" }, // 👈 НАШ ПУНКТ
            { label: "Месячная доходность", path: "/budget/income" },
            { label: "Месячные платежи", path: "/budget/payments" },
        ],
    },
    {
        title: "Инвестиции",
        items: [
            { label: "Расчёт средней цены актива", path: "/investments/average-price" },
            { label: "Расчёт инфляции по капиталу", path: "/investments/inflation" },
            { label: "Мониторинг акций", path: "/investments/monitoring" },
        ],
    },
    {
        title: "Банки",
        items: [
            { label: "Расчёт покупки с учётом вклада", path: "/banks/installment-calc" },
            { label: "Мониторинг продуктов", path: "/banks/monitoring" },
        ],
    },
    {
        title: "Личное",
        items: [{ label: "Список задач", path: "/personal/tasks" }],
    },
];

const Sidebar: React.FC = () => {
    const [open, setOpen] = useState(false);
    const navigate = useNavigate();

    const handleToggle = () => setOpen((prev) => !prev);

    const handleNavigate = (path: string) => {
        navigate(path);
        setOpen(false);
    };

    return (
        <>
            {/* Кнопка-бургер слева сверху */}
            <button
                type="button"
                className="sidebar-burger"
                onClick={handleToggle}
                aria-label="Открыть меню"
            >
                <span />
                <span />
                <span />
            </button>

            {/* Затемнённый фон при открытом меню */}
            <div
                className={`sidebar-backdrop ${open ? "sidebar-backdrop--open" : ""}`}
                onClick={handleToggle}
            />

            {/* Сам выезжающий сайдбар */}
            <aside className={`sidebar ${open ? "sidebar--open" : ""}`}>
                <div className="sidebar-header">
                    <div className="sidebar-logo-mark">МГМ</div>
                    <div className="sidebar-logo-text">My Game Money</div>
                </div>

                <nav className="sidebar-nav">
                    {sections.map((section) => (
                        <div key={section.title} className="sidebar-section">
                            <div className="sidebar-section-title">{section.title}</div>
                            <ul className="sidebar-section-list">
                                {section.items.map((item) => (
                                    <li key={item.path}>
                                        <button
                                            type="button"
                                            className="sidebar-link"
                                            onClick={() => handleNavigate(item.path)}
                                        >
                                            <span className="sidebar-link-text">{item.label}</span>
                                            <span className="sidebar-link-corner-left" />
                                            <span className="sidebar-link-corner-right" />
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    ))}
                </nav>
            </aside>
        </>
    );
};

export default Sidebar;
