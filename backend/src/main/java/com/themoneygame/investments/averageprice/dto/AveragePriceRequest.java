package com.themoneygame.investments.averageprice.dto;

import com.themoneygame.investments.averageprice.CalculationMode;

import java.math.BigDecimal;

public class AveragePriceRequest {

    private CalculationMode mode;

    private BigDecimal currentAveragePrice;
    private BigDecimal currentQuantity;
    private BigDecimal investAmount;

    // для режима TARGET_AVERAGE
    private BigDecimal desiredAveragePrice;

    // для режима NEW_AVERAGE
    private BigDecimal newPrice;

    // ---- геттеры/сеттеры ----

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
}
