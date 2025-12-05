package com.themoneygame.investments.monitoring.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_assets")
public class MarketAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32, unique = true)
    private String symbol;       // тикер: "SBERP", "TATNP", "LKOH", "GOLD", "BRENT"

    @Column(nullable = false, length = 128)
    private String name;         // "Сбербанк-п", "Татнефть-п", "Золото", "Нефть Brent"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType type;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal lastPrice;      // последняя цена

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal changeAbsolute; // изменение в рублях/долларах

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal changePercent;  // изменение в %

    @Column(nullable = false, length = 8)
    private String currency;           // "RUB", "USD"

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // --- getters / setters ---

    public Long getId() { return id; }

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
