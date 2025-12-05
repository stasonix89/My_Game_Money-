// src/api/budgetPaymentsApi.ts
import axiosClient from "./axiosClient";

export type MonthlyPaymentStatus = "PLANNED" | "PAID";

export interface MonthlyPaymentDto {
    id: number;
    name: string;             // title из доменной сущности
    category: string | null;  // сейчас всегда null, на фронте показываем "—"
    amount: number;           // BigDecimal → number
    paymentDate: string;      // ISO-строка "YYYY-MM-DD"
    status: MonthlyPaymentStatus;
}

/**
 * Получить все платежи пользователя.
 * Фильтрация по месяцу/году и статусу делается на фронте.
 *
 * GET /api/budget/payments
 */
export async function fetchMonthlyPayments(): Promise<MonthlyPaymentDto[]> {
    const response = await axiosClient.get<MonthlyPaymentDto[]>("/api/budget/payments");
    return response.data;
}

/**
 * Создать новый месячный платёж.
 *
 * Бэкенд принимает доменную сущность MonthlyPayment:
 * {
 *   "account": { "id": 123 },
 *   "paymentDate": "2025-11-10",
 *   "amount": 45000.00,
 *   "title": "Ипотека"
 * }
 *
 * POST /api/budget/payments
 */
export async function createMonthlyPayment(payload: {
    accountId: number;
    paymentDate: string; // "YYYY-MM-DD"
    amount: number;
    title: string;
}): Promise<MonthlyPaymentDto> {
    const body = {
        account: { id: payload.accountId },
        paymentDate: payload.paymentDate,
        amount: payload.amount,
        title: payload.title,
    };

    const response = await axiosClient.post<MonthlyPaymentDto>("/api/budget/payments", body);
    return response.data;
}

/**
 * Отметить платёж как оплаченный.
 *
 * POST /api/budget/payments/{id}/mark-paid
 *
 * Бэкенд:
 *  - ставит paid = true
 *  - создаёт Transaction типа TRANSFER
 *  - возвращает обновлённый MonthlyPaymentDto
 */
export async function markMonthlyPaymentPaid(id: number): Promise<MonthlyPaymentDto> {
    const response = await axiosClient.post<MonthlyPaymentDto>(`/api/budget/payments/${id}/mark-paid`);
    return response.data;
}

/**
 * Отменить оплату платежа (откат TRANSFER).
 *
 * POST /api/budget/payments/{id}/unmark-paid
 *
 * Бэкенд:
 *  - ставит paid = false
 *  - находит и удаляет соответствующую TRANSFER-транзакцию
 *  - возвращает MonthlyPaymentDto со статусом "PLANNED"
 */
export async function unmarkMonthlyPaymentPaid(id: number): Promise<MonthlyPaymentDto> {
    const response = await axiosClient.post<MonthlyPaymentDto>(`/api/budget/payments/${id}/unmark-paid`);
    return response.data;
}
