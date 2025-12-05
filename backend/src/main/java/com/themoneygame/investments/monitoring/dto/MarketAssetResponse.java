package com.themoneygame.investments.monitoring.dto;

import com.themoneygame.investments.monitoring.domain.AssetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MarketAssetResponse {

    private Long id;
    private String symbol;
    private String name;
    private AssetType type;

    private BigDecimal lastPrice;
    private BigDecimal changeAbsolute;
    private BigDecimal changePercent;
    private String currency;
    private LocalDateTime updatedAt;

    // --- getters / setters ---

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }

    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public AssetType getType() { return type; }

    public void setType(AssetType type) { this.type = type; }

    public BigDecimal getLastPrice() { return lastPrice; }

    public void setLastPrice(BigDecimal lastPrice) { this.lastPrice = lastPrice; }

    public BigDecimal getChangeAbsolute() { return changeAbsolute; }

    public void setChangeAbsolute(BigDecimal changeAbsolute) { this.changeAbsolute = changeAbsolute; }

    public BigDecimal getChangePercent() { return changePercent; }

    public void setChangePercent(BigDecimal changePercent) { this.changePercent = changePercent; }

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
