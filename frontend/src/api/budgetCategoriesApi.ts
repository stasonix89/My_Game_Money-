// src/api/budgetCategoriesApi.ts
import axiosClient from "./axiosClient";
import type { CategoryDto, OperationKind } from "../types/budget/categories";

// Получить все категории пользователя
export async function fetchCategories(): Promise<CategoryDto[]> {
    const res = await axiosClient.get<CategoryDto[]>("/api/budget/categories");
    return res.data;
}

// Создать новую категорию
export async function createCategory(payload: {
    name: string;
    operationType: OperationKind;
}): Promise<CategoryDto> {
    const res = await axiosClient.post<CategoryDto>(
        "/api/budget/categories",
        payload
    );
    return res.data;
}

// Обновить (переименовать / сменить тип)
export async function updateCategory(
    id: number,
    payload: { name: string; operationType: OperationKind }
): Promise<CategoryDto> {
    const res = await axiosClient.put<CategoryDto>(
        `/api/budget/categories/${id}`,
        payload
    );
    return res.data;
}

// Удалить категорию
export async function deleteCategory(id: number): Promise<void> {
    await axiosClient.delete(`/api/budget/categories/${id}`);
}
