package com.themoneygame.investments.monitoring.dto;

public class WatchlistModifyRequest {

    private String symbol;
    private String customName; // можно null

    public String getSymbol() { return symbol; }

    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCustomName() { return customName; }

    public void setCustomName(String customName) { this.customName = customName; }
}
