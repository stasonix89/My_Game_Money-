// src/types/budget/transactions.ts

// Тип операции — базовый enum/union, используемый по всему фронтенду
export type TransactionType = "INCOME" | "EXPENSE" | "TRANSFER";

// DTO одной операции, как приходит с backend
export type TransactionDto = {
    id: number;
    date: string;           // формат "YYYY-MM-DD"
    type: TransactionType;  // INCOME | EXPENSE | TRANSFER
    category: string;       // имя категории (человекочитаемое)
    fromAccountId: number | null;
    toAccountId: number | null;
    amount: number;         // всегда положительное число (фронт сам ставит знак)
};

// Payload для создания операции
export type CreateTransactionRequest = {
    date: string;           // "YYYY-MM-DD"
    type: TransactionType;
    category: string;
    fromAccountId: number | null;
    toAccountId: number | null;
    amount: number;         // положительное число
};
