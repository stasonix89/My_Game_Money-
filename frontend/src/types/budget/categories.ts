// src/types/budget/categories.ts

// Тип операции, к которой относится категория
export type OperationKind = "INCOME" | "EXPENSE" | "TRANSFER";

export type CategoryDto = {
    id: number;
    name: string;
    operationType: OperationKind; // для какого типа операции эта категория
};
