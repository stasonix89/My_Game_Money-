// src/api/budgetBanksApi.ts
import axiosClient from "./axiosClient";
import type { BankDto } from "../types/budget/banks";

// Получить список банков
export async function fetchBanks(): Promise<BankDto[]> {
    const res = await axiosClient.get<BankDto[]>("/api/budget/banks");
    return res.data;
}

// Создать новый банк
export async function createBank(name: string): Promise<BankDto> {
    const res = await axiosClient.post<BankDto>("/api/budget/banks", { name });
    return res.data;
}

// Опционально (на будущее) — удалить банк
export async function deleteBank(id: number): Promise<void> {
    await axiosClient.delete(`/api/budget/banks/${id}`);
}
