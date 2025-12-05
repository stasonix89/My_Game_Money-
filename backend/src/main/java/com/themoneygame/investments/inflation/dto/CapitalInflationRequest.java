package com.themoneygame.investments.inflation.dto;

import java.math.BigDecimal;

public class CapitalInflationRequest {

    private BigDecimal initialCapital;      // начальный капитал
    private BigDecimal monthlyContribution; // пополнение в месяц
    private Integer years;                  // срок в годах

    private BigDecimal averageReturn;       // средняя доходность инвестиций (% годовых)

    private boolean compareWithDeposit;     // галочка "сравнить со вкладом"
    private BigDecimal depositRate;         // ставка по вкладу (% годовых)

    private boolean useInflation;           // учитывать инфляцию
    private BigDecimal inflationRate;       // инфляция (% годовых)

    // --- геттеры / сеттеры ---

    public BigDecimal getInitialCapital() {
        return initialCapital;
    }

    public void setInitialCapital(BigDecimal initialCapital) {
        this.initialCapital = initialCapital;
    }

    public BigDecimal getMonthlyContribution() {
        return monthlyContribution;
    }

    public void setMonthlyContribution(BigDecimal monthlyContribution) {
        this.monthlyContribution = monthlyContribution;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public BigDecimal getAverageReturn() {
        return averageReturn;
    }

    public void setAverageReturn(BigDecimal averageReturn) {
        this.averageReturn = averageReturn;
    }

    public boolean isCompareWithDeposit() {
        return compareWithDeposit;
    }

    public void setCompareWithDeposit(boolean compareWithDeposit) {
        this.compareWithDeposit = compareWithDeposit;
    }

    public BigDecimal getDepositRate() {
        return depositRate;
    }

    public void setDepositRate(BigDecimal depositRate) {
        this.depositRate = depositRate;
    }

    public boolean isUseInflation() {
        return useInflation;
    }

    public void setUseInflation(boolean useInflation) {
        this.useInflation = useInflation;
    }

    public BigDecimal getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(BigDecimal inflationRate) {
        this.inflationRate = inflationRate;
    }
}
