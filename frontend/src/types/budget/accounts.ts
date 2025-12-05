// src/types/budget/accounts.ts

export type AccountType =
    | "DEBIT"
    | "CREDIT_PURCHASE"
    | "CREDIT_CASH"
    | "ASSET";

export type AccountDto = {
    id: number;
    bankName: string;
    name: string;
    type: AccountType;
    limit: number | null;       // кредитный лимит (только для кредиток)
    balance: number;            // текущий баланс / стоимость
    mainForPayments: boolean;   // основная карта для списаний
    forWithdraw: boolean;       // используется как счет для снятия
};
