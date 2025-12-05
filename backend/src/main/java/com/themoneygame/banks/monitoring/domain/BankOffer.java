package com.themoneygame.banks.monitoring.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_offers")
public class BankOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName;

    @Enumerated(EnumType.STRING)
    private ProductType type; // DEPOSIT / CREDIT / DEBIT

    // Универсальные поля
    private String title;         // "Вклад Максимальный", "Кредитная карта 120 дней"
    private String description;   // удобное поле для фронта

    // Цифровые параметры для сортировки
    private BigDecimal interestRate;  // ставка по вкладу / кредиту / остатку
    private BigDecimal cashback;      // кешбэк для карт
    private BigDecimal limitAmount;   // кредитный лимит
    private Integer gracePeriod;      // льготный период для кредиток
    private BigDecimal minDeposit;    // минимальная сумма для вклада

    private LocalDateTime updatedAt = LocalDateTime.now();

    // --- getters / setters ---
    public Long getId() { return id; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public ProductType getType() { return type; }
    public void setType(ProductType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public BigDecimal getCashback() { return cashback; }
    public void setCashback(BigDecimal cashback) { this.cashback = cashback; }

    public BigDecimal getLimitAmount() { return limitAmount; }
    public void setLimitAmount(BigDecimal limitAmount) { this.limitAmount = limitAmount; }

    public Integer getGracePeriod() { return gracePeriod; }
    public void setGracePeriod(Integer gracePeriod) { this.gracePeriod = gracePeriod; }

    public BigDecimal getMinDeposit() { return minDeposit; }
    public void setMinDeposit(BigDecimal minDeposit) { this.minDeposit = minDeposit; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
