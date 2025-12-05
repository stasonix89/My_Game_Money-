package com.themoneygame.investments.inflation.dto;

import java.math.BigDecimal;

/**
 * Одна точка на графике "капитал по годам".
 */
public class CapitalInflationYearPoint {

    private int year; // 1,2,3,... N лет с начала сценария

    private BigDecimal investmentValue;   // капитал в инвестициях
    private BigDecimal depositValue;      // капитал на вкладе (если сравниваем)
    private BigDecimal inflationAdjusted; // капитал с учётом инфляции (если отмечено)

    // --- геттеры / сеттеры ---

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getInvestmentValue() {
        return investmentValue;
    }

    public void setInvestmentValue(BigDecimal investmentValue) {
        this.investmentValue = investmentValue;
    }

    public BigDecimal getDepositValue() {
        return depositValue;
    }

    public void setDepositValue(BigDecimal depositValue) {
        this.depositValue = depositValue;
    }

    public BigDecimal getInflationAdjusted() {
        return inflationAdjusted;
    }

    public void setInflationAdjusted(BigDecimal inflationAdjusted) {
        this.inflationAdjusted = inflationAdjusted;
    }
}
