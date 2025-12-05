// src/main/java/com/themoneygame/budget/domain/Transaction.java
package com.themoneygame.budget.domain;

import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.enums.CategoryType;
import com.themoneygame.budget.domain.enums.TransactionType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CategoryType category;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;

    public Transaction() {
    }

    public Transaction(
            User user,
            TransactionType type,
            CategoryType category,
            BigDecimal amount,
            Account account,
            LocalDate date,
            String description
    ) {
        this.user = user;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.account = account;
        this.date = date;
        this.description = description;
    }

    // ===== Getters / Setters =====

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getAmount() {
        return amount != null ? amount : BigDecimal.ZERO;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
