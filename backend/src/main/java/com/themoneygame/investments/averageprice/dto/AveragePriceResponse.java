package com.themoneygame.investments.averageprice.dto;

import com.themoneygame.investments.averageprice.CalculationMode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AveragePriceResponse {

    private Long id;
    private CalculationMode mode;

    private BigDecimal currentAveragePrice;
    private BigDecimal currentQuantity;
    private BigDecimal investAmount;

    private BigDecimal desiredAveragePrice;
    private BigDecimal newPrice;

    private BigDecimal resultAveragePrice;
    private BigDecimal resultQuantity;
    private BigDecimal resultTotalAmount;

    private BigDecimal requiredBuyPrice;

    private LocalDateTime createdAt;

    // ---- геттеры/сеттеры ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculationMode getMode() {
        return mode;
    }

    public void setMode(CalculationMode mode) {
        this.mode = mode;
    }

    public BigDecimal getCurrentAveragePrice() {
        return currentAveragePrice;
    }

    public void setCurrentAveragePrice(BigDecimal currentAveragePrice) {
        this.currentAveragePrice = currentAveragePrice;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public BigDecimal getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(BigDecimal investAmount) {
        this.investAmount = investAmount;
    }

    public BigDecimal getDesiredAveragePrice() {
        return desiredAveragePrice;
    }

    public void setDesiredAveragePrice(BigDecimal desiredAveragePrice) {
        this.desiredAveragePrice = desiredAveragePrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public BigDecimal getResultAveragePrice() {
        return resultAveragePrice;
    }

    public void setResultAveragePrice(BigDecimal resultAveragePrice) {
        this.resultAveragePrice = resultAveragePrice;
    }

    public BigDecimal getResultQuantity() {
        return resultQuantity;
    }

    public void setResultQuantity(BigDecimal resultQuantity) {
        this.resultQuantity = resultQuantity;
    }

    public BigDecimal getResultTotalAmount() {
        return resultTotalAmount;
    }

    public void setResultTotalAmount(BigDecimal resultTotalAmount) {
        this.resultTotalAmount = resultTotalAmount;
    }

    public BigDecimal getRequiredBuyPrice() {
        return requiredBuyPrice;
    }

    public void setRequiredBuyPrice(BigDecimal requiredBuyPrice) {
        this.requiredBuyPrice = requiredBuyPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
