// src/main/java/com/themoneygame/budget/domain/Account.java
package com.themoneygame.budget.domain;

import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.enums.AccountType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    /**
     * Отдельное поле для номера карты/счёта.
     * В БД: column "card_number" NOT NULL
     */
    @Column(name = "card_number", nullable = false, length = 100)
    private String cardNumber = "";

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AccountType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "credit_limit", precision = 19, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "used_limit", precision = 19, scale = 2)
    private BigDecimal usedLimit;

    @Column(name = "main_for_payments", nullable = false)
    private boolean mainForPayments = false;

    /**
     * Колонка is_primary в БД (NOT NULL).
     * Пока логически можно считать "основным счётом" или просто технический флаг.
     */
    @Column(name = "is_primary", nullable = false)
    private boolean primaryAccount = false;

    @Column(name = "credit_for_cash_withdrawal", nullable = false)
    private boolean creditForCashWithdrawal = false;

    // ---------------- Getters / Setters ----------------

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = (cardNumber != null) ? cardNumber : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if (this.cardNumber == null || this.cardNumber.isEmpty()) {
            this.cardNumber = (name != null) ? name : "";
        }
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance != null ? balance : BigDecimal.ZERO;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getUsedLimit() {
        return usedLimit;
    }

    public void setUsedLimit(BigDecimal usedLimit) {
        this.usedLimit = usedLimit;
    }

    public boolean isMainForPayments() {
        return mainForPayments;
    }

    public void setMainForPayments(boolean mainForPayments) {
        this.mainForPayments = mainForPayments;
    }

    public boolean isPrimaryAccount() {
        return primaryAccount;
    }

    public void setPrimaryAccount(boolean primaryAccount) {
        this.primaryAccount = primaryAccount;
    }

    public boolean isCreditForCashWithdrawal() {
        return creditForCashWithdrawal;
    }

    public void setCreditForCashWithdrawal(boolean creditForCashWithdrawal) {
        this.creditForCashWithdrawal = creditForCashWithdrawal;
    }

    // Удобные методы

    public boolean isDebt() {
        return type == AccountType.CREDIT_PURCHASE || type == AccountType.CREDIT_CASH;
    }

    public boolean isAsset() {
        return type == AccountType.ASSET;
    }
}
