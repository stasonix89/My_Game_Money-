package com.themoneygame.investments.monitoring.domain;

import com.themoneygame.auth.domain.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_watchlist",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "symbol"})
)
public class UserWatchlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, length = 32)
    private String symbol;           // тикер

    @Column(length = 128)
    private String customName;       // если пользователь хочет своё название

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // --- getters / setters ---

    public Long getId() { return id; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public String getSymbol() { return symbol; }

    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCustomName() { return customName; }

    public void setCustomName(String customName) { this.customName = customName; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
