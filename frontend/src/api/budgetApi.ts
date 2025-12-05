// src/api/budgetApi.ts
import axiosClient from "./axiosClient";

/**
 * Получить общий дашборд бюджета
 * GET /api/budget/dashboard
 */
export async function getBudgetDashboard(month?: string) {
    const response = await axiosClient.get("/api/budget/dashboard", {
        params: month ? { month } : {},
    });
    return response.data;
}

/**
 * Получить список счетов / учет финансов
 * GET /api/budget/finance
 */
export async function getFinanceAccounts() {
    const response = await axiosClient.get("/api/budget/finance");
    return response.data;
}

/**
 * Получить месячную доходность
 * GET /api/budget/income
 *
 * (простая версия; для более детальной модели с extraIncomes
 * мы используем отдельный клиент budgetIncomeApi.ts)
 */
export async function getMonthlyIncome(month?: string) {
    const response = await axiosClient.get("/api/budget/income", {
        params: month ? { month } : {},
    });
    return response.data;
}

/**
 * Сохранить (или обновить) месячную доходность
 * POST /api/budget/income
 */
export async function saveMonthlyIncome(payload: {
    month: string;
    dividends: number;
    depositsInterest: number;
}) {
    const response = await axiosClient.post("/api/budget/income", payload);
    return response.data;
}

/**
 * Получить таблицу месячных платежей
 * GET /api/budget/payments
 */
export async function getMonthlyPayments(month?: string) {
    const response = await axiosClient.get("/api/budget/payments", {
        params: month ? { month } : {},
    });
    return response.data;
}

/**
 * Отметить платёж как оплаченный
 * POST /api/budget/payments/{id}/paid
 */
export async function markPaymentPaid(paymentId: number) {
    const response = await axiosClient.post(
        `/api/budget/payments/${paymentId}/paid`
    );
    return response.data;
}

/**
 * Создать новый месячный платёж
 * POST /api/budget/payments
 */
export async function createMonthlyPayment(payload: {
    bank: string;
    card: string;
    paymentDate: string; // YYYY-MM-DD
    amount: number;
}) {
    const response = await axiosClient.post(`/api/budget/payments`, payload);
    return response.data;
}

/**
 * Создать транзакцию
 * POST /api/budget/transactions
 */
export async function addTransaction(payload: {
    amount: number;
    description: string;
    accountId: number;
    transactionDate: string; // YYYY-MM-DD
}) {
    const response = await axiosClient.post(`/api/budget/transactions`, payload);
    return response.data;
}
