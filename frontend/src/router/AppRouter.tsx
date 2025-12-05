// src/router/AppRouter.tsx
import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import LoginPage from "../pages/auth/LoginPage";
import SignupPage from "../pages/auth/SignupPage";

import BudgetDashboardPage from "../pages/budget/BudgetDashboardPage";
import BudgetFinancePage from "../pages/budget/BudgetFinancePage";
import BudgetIncomePage from "../pages/budget/BudgetIncomePage";
import BudgetPaymentsPage from "../pages/budget/BudgetPaymentsPage";

import PersonalTasksPage from "../pages/personal/PersonalTasksPage";

import InstallmentCalcPage from "../pages/banks/InstallmentCalcPage";
import BankMonitoringPage from "../pages/banks/BankMonitoringPage";

import AveragePricePage from "../pages/investments/AveragePricePage";
import InflationCalcPage from "../pages/investments/InflationCalcPage";
import InvestmentsMonitoringPage from "../pages/investments/InvestmentsMonitoringPage";

import PrivateRoute from "./PrivateRoute";
import { useAuthStore } from "../store/authStore";

const AppRouter: React.FC = () => {
    const { isLoggedIn, loading } = useAuthStore();

    return (
        <Routes>
            {/* LOGIN / SIGNUP */}
            <Route
                path="/login"
                element={
                    !loading && isLoggedIn ? (
                        <Navigate to="/budget/dashboard" replace />
                    ) : (
                        <LoginPage />
                    )
                }
            />
            <Route
                path="/signup"
                element={
                    !loading && isLoggedIn ? (
                        <Navigate to="/budget/dashboard" replace />
                    ) : (
                        <SignupPage />
                    )
                }
            />

            {/* BUDGET */}
            <Route
                path="/budget/dashboard"
                element={
                    <PrivateRoute>
                        <BudgetDashboardPage />
                    </PrivateRoute>
                }
            />

            <Route
                path="/budget/finance"
                element={
                    <PrivateRoute>
                        <BudgetFinancePage />
                    </PrivateRoute>
                }
            />

            <Route
                path="/budget/income"
                element={
                    <PrivateRoute>
                        <BudgetIncomePage />
                    </PrivateRoute>
                }
            />

            <Route
                path="/budget/payments"
                element={
                    <PrivateRoute>
                        <BudgetPaymentsPage />
                    </PrivateRoute>
                }
            />

            {/* PERSONAL */}
            <Route
                path="/personal/tasks"
                element={
                    <PrivateRoute>
                        <PersonalTasksPage />
                    </PrivateRoute>
                }
            />

            {/* BANKS */}
            <Route
                path="/banks/installment-calc"
                element={
                    <PrivateRoute>
                        <InstallmentCalcPage />
                    </PrivateRoute>
                }
            />
            <Route
                path="/banks/monitoring"
                element={
                    <PrivateRoute>
                        <BankMonitoringPage />
                    </PrivateRoute>
                }
            />

            {/* INVESTMENTS */}
            <Route
                path="/investments/average-price"
                element={
                    <PrivateRoute>
                        <AveragePricePage />
                    </PrivateRoute>
                }
            />

            <Route
                path="/investments/inflation"
                element={
                    <PrivateRoute>
                        <InflationCalcPage />
                    </PrivateRoute>
                }
            />

            <Route
                path="/investments/monitoring"
                element={
                    <PrivateRoute>
                        <InvestmentsMonitoringPage />
                    </PrivateRoute>
                }
            />

            {/* DEFAULT */}
            <Route path="*" element={<Navigate to="/budget/dashboard" />} />
        </Routes>
    );
};

export default AppRouter;
