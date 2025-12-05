// src/api/budgetTransactionsApi.ts
import axiosClient from "./axiosClient";
import type {
    TransactionDto,
    CreateTransactionRequest,
} from "../types/budget/transactions";

// Загрузка операций за конкретный месяц/год
export async function fetchTransactions(params: {
    year: number;
    month: number; // 1–12
}): Promise<TransactionDto[]> {
    const res = await axiosClient.get<TransactionDto[]>(
        "/api/budget/transactions",
        {
            params: {
                year: params.year,
                month: params.month,
            },
        }
    );
    return res.data;
}

// Создание операции
export async function createTransaction(
    payload: CreateTransactionRequest
): Promise<TransactionDto> {
    const res = await axiosClient.post<TransactionDto>(
        "/api/budget/transactions",
        payload
    );
    return res.data;
}

// Опционально: удаление операции
export async function deleteTransaction(id: number): Promise<void> {
    await axiosClient.delete(`/api/budget/transactions/${id}`);
}
