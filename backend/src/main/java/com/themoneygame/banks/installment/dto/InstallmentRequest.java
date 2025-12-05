package com.themoneygame.banks.installment.dto;

import java.math.BigDecimal;

public class InstallmentRequest {

    // сумма покупки
    private BigDecimal purchaseAmount;

    // срок рассрочки в месяцах
    private Integer months;

    // ставка вклада (% годовых)
    private BigDecimal depositRate;

    // учитывать кешбэк в расчёте выгоды (добавлять к первым двум полосам)
    private boolean includeCashback;

    // true = значение cashbackValue — это %, false = фиксированная сумма в рублях
    private boolean cashbackPercent;

    // либо % кешбэка, либо рубли — в зависимости от cashbackPercent
    private BigDecimal cashbackValue;

    // -------- getters / setters --------

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public BigDecimal getDepositRate() {
        return depositRate;
    }

    public void setDepositRate(BigDecimal depositRate) {
        this.depositRate = depositRate;
    }

    public boolean isIncludeCashback() {
        return includeCashback;
    }

    public void setIncludeCashback(boolean includeCashback) {
        this.includeCashback = includeCashback;
    }

    public boolean isCashbackPercent() {
        return cashbackPercent;
    }

    public void setCashbackPercent(boolean cashbackPercent) {
        this.cashbackPercent = cashbackPercent;
    }

    public BigDecimal getCashbackValue() {
        return cashbackValue;
    }

    public void setCashbackValue(BigDecimal cashbackValue) {
        this.cashbackValue = cashbackValue;
    }
}
