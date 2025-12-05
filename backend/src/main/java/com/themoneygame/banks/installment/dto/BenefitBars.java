package com.themoneygame.banks.installment.dto;

import java.math.BigDecimal;

/**
 * Данные для трёх "полос" выгоды:
 *  - равные платежи
 *  - минимальные платежи
 *  - кешбэк
 */
public class BenefitBars {

    private BigDecimal equal;    // выгода при равных платежах
    private BigDecimal min;      // выгода при минимальных платежах
    private BigDecimal cashback; // сумма кешбэка

    public BigDecimal getEqual() {
        return equal;
    }

    public void setEqual(BigDecimal equal) {
        this.equal = equal;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getCashback() {
        return cashback;
    }

    public void setCashback(BigDecimal cashback) {
        this.cashback = cashback;
    }
}
