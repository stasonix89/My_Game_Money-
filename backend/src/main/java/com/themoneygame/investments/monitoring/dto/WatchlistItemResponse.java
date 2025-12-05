package com.themoneygame.investments.monitoring.dto;

import java.time.LocalDateTime;

public class WatchlistItemResponse {

    private Long id;
    private String symbol;
    private String name;       // либо имя актива, либо customName
    private LocalDateTime createdAt;

    // --- getters / setters ---

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getSymbol() { return symbol; }

    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
