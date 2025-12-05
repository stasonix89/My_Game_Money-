// src/api/budgetIncomeApi.ts
import axiosClient from "./axiosClient";

export type ExtraIncomeDto = {
    label: string;
    amount: number;
};

export type BudgetIncomeRequest = {
    year: number;
    month: number; // 1-12
    yearlyDividends: number;
    depositAmount: number;
    depositRateYearly: number;
    salaryMonthly: number;
    extraIncomes: ExtraIncomeDto[];
};

export type BudgetIncomeHistoryPoint = {
    year: number;
    month: number;
    totalMonthlyIncome: number;
};

export type BudgetIncomeResponse = {
    year: number;
    month: number;

    yearlyDividends: number;
    depositAmount: number;
    depositRateYearly: number;
    salaryMonthly: number;
    extraIncomes: ExtraIncomeDto[];

    dividendsMonthly: number;
    depositInterestMonthly: number;
    extraIncomesMonthly: number;
    totalMonthlyIncome: number;

    history: BudgetIncomeHistoryPoint[];
};

export const budgetIncomeApi = {
    async getIncome(year: number, month: number): Promise<BudgetIncomeResponse> {
        const response = await axiosClient.get<BudgetIncomeResponse>(
            "/api/budget/income",
            {
                params: { year, month },
            }
        );
        return response.data;
    },

    async saveIncome(payload: BudgetIncomeRequest): Promise<BudgetIncomeResponse> {
        const response = await axiosClient.post<BudgetIncomeResponse>(
            "/api/budget/income",
            payload
        );
        return response.data;
    },
};
