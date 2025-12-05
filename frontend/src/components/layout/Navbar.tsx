// src/components/layout/Navbar.tsx
import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./navbar.css";

type NavSubItem = {
    label: string;
    path: string;
};

type NavItem = {
    label: string;
    basePath: string;
    defaultPath: string;
    subItems?: NavSubItem[];
};

const NAV_ITEMS: NavItem[] = [
    {
        label: "Инвестиции",
        basePath: "/investments",
        defaultPath: "/investments/average-price",
        subItems: [
            { label: "Расчёт средней цены актива", path: "/investments/average-price" },
            { label: "Расчёт инфляции по капиталу", path: "/investments/inflation" },
            { label: "Мониторинг акций", path: "/investments/monitoring" },
        ],
    },
    {
        label: "Банки",
        basePath: "/banks",
        defaultPath: "/banks/installment-calc",
        subItems: [
            { label: "Расчёт покупки с учётом вклада", path: "/banks/installment-calc" },
            { label: "Мониторинг продуктов", path: "/banks/monitoring" },
        ],
    },
    {
        label: "Личное",
        basePath: "/personal",
        defaultPath: "/personal/tasks",
        subItems: [{ label: "Список задач", path: "/personal/tasks" }],
    },
    {
        label: "Бюджет",
        basePath: "/budget",
        defaultPath: "/budget/dashboard",
        subItems: [
            { label: "Общий дашборд", path: "/budget/dashboard" },
            { label: "Учёт финансов", path: "/budget/finance" },
            { label: "Месячная доходность", path: "/budget/income" },
            { label: "Месячные платежи", path: "/budget/payments" },
        ],
    },
];

const Navbar: React.FC = () => {
    const navigate = useNavigate();
    const location = useLocation();

    // какое меню в нижней навигации сейчас раскрыто (по basePath)
    const [openMenuBasePath, setOpenMenuBasePath] = React.useState<string | null>(
        null
    );

    const isItemActive = (item: NavItem) =>
        location.pathname.startsWith(item.basePath);

    // Клик по названию сайта → просто переход на главную
    const handleTitleClick = () => {
        navigate("/budget/dashboard");
        setOpenMenuBasePath(null);
    };

    // Клик по пункту нижнего меню (Инвестиции/Банки/Личное/Бюджет)
    const handleNavClick = (item: NavItem) => {
        setOpenMenuBasePath((prev) =>
            prev === item.basePath ? null : item.basePath
        );
    };

    const handleNavSubItemClick = (path: string) => {
        navigate(path);
        setOpenMenuBasePath(null);
    };

    return (
        <header className="navbar">
            {/* ВЕРХНЯЯ ПОЛОСА С ЗОЛОТЫМ НАЗВАНИЕМ */}
            <div className="navbar-top">
                <div className="navbar-title-container" onClick={handleTitleClick}>
                    <span className="navbar-title-glow">MY GAME MONEY</span>
                </div>
            </div>

            {/* НИЖНЯЯ ПОЛОСА НАВИГАЦИИ */}
            <nav className="navbar-nav">
                {NAV_ITEMS.map((item) => {
                    const isOpen = openMenuBasePath === item.basePath;

                    return (
                        <div key={item.label} className="navbar-nav-item">
                            <button
                                type="button"
                                className={
                                    "navbar-nav-link" +
                                    (isItemActive(item) ? " navbar-nav-link--active" : "")
                                }
                                onClick={() => handleNavClick(item)}
                            >
                                {item.label}
                            </button>

                            {item.subItems && isOpen && (
                                <div className="navbar-dropdown">
                                    {item.subItems.map((sub) => (
                                        <button
                                            key={sub.path}
                                            type="button"
                                            className="navbar-dropdown-link"
                                            onClick={() => handleNavSubItemClick(sub.path)}
                                        >
                                            <span className="dropdown-corner dropdown-corner--left" />
                                            <span className="dropdown-text">{sub.label}</span>
                                            <span className="dropdown-corner dropdown-corner--right" />
                                        </button>
                                    ))}
                                </div>
                            )}
                        </div>
                    );
                })}
            </nav>
        </header>
    );
};

export default Navbar;
