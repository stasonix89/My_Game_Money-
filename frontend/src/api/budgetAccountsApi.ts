// src/api/budgetAccountsApi.ts
import axiosClient from "./axiosClient";
import type { AccountDto } from "../types/budget/accounts";

// GET — получить все счета пользователя
export async function fetchAccounts(): Promise<AccountDto[]> {
    const res = await axiosClient.get<AccountDto[]>("/api/budget/accounts");
    return res.data;
}

// POST — создать новый счёт / карту / актив
export async function createAccount(
    dto: Omit<AccountDto, "id">
): Promise<AccountDto> {
    const res = await axiosClient.post<AccountDto>("/api/budget/accounts", dto);
    return res.data;
}

// PUT — обновить счёт
export async function updateAccount(
    id: number,
    dto: Omit<AccountDto, "id">
): Promise<AccountDto> {
    const res = await axiosClient.put<AccountDto>(
        `/api/budget/accounts/${id}`,
        dto
    );
    return res.data;
}

// DELETE — удалить счёт
export async function deleteAccount(id: number): Promise<void> {
    await axiosClient.delete(`/api/budget/accounts/${id}`);
}

// GET — основная карта для списаний
export async function fetchMainPaymentAccount(): Promise<AccountDto> {
    const res = await axiosClient.get<AccountDto>(
        "/api/budget/accounts/main-for-payments"
    );
    return res.data;
}

// POST — сделать счёт основным для списаний
export async function setMainForPayments(id: number): Promise<AccountDto> {
    const res = await axiosClient.post<AccountDto>(
        `/api/budget/accounts/${id}/set-main-for-payments`
    );
    return res.data;
}
