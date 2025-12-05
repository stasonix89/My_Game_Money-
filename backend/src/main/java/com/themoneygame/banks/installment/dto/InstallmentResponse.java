package com.themoneygame.banks.installment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Ответ по расчёту рассрочки:
 *  - три значения выгоды
 *  - структура для полос
 *  - id/createdAt для истории
 */
public class InstallmentResponse {

    private Long id;
    private LocalDateTime createdAt;

    private BigDecimal equalBenefit;
    private BigDecimal minBenefit;
    private BigDecimal cashbackBenefit;

    private BenefitBars bars;

    // ------- getters / setters -------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public BigDecimal getEqualBenefit() {
        return equalBenefit;
    }

    public void setEqualBenefit(BigDecimal equalBenefit) {
        this.equalBenefit = equalBenefit;
    }

    public BigDecimal getMinBenefit() {
        return minBenefit;
    }

    public void setMinBenefit(BigDecimal minBenefit) {
        this.minBenefit = minBenefit;
    }

    public BigDecimal getCashbackBenefit() {
        return cashbackBenefit;
    }

    public void setCashbackBenefit(BigDecimal cashbackBenefit) {
        this.cashbackBenefit = cashbackBenefit;
    }

    public BenefitBars getBars() {
        return bars;
    }

    public void setBars(BenefitBars bars) {
        this.bars = bars;
    }
}
