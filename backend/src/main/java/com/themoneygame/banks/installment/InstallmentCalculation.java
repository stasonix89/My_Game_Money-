package com.themoneygame.banks.installment;

import com.themoneygame.auth.domain.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "installment_calculations")
public class InstallmentCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal purchaseAmount;

    @Column(nullable = false)
    private Integer months;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal depositRate;

    @Column(nullable = false)
    private boolean includeCashback;

    @Column(nullable = false)
    private boolean cashbackPercent;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal cashbackValue;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal benefitEqual;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal benefitMin;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal benefitCashback;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ------- getters / setters -------

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(BigDecimal purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public Integer getMonths() {
        return months;
    }

    public void setMonths(Integer months) {
        this.months = months;
    }

    public BigDecimal getDepositRate() {
        return depositRate;
    }

    public void setDepositRate(BigDecimal depositRate) {
        this.depositRate = depositRate;
    }

    public boolean isIncludeCashback() {
        return includeCashback;
    }

    public void setIncludeCashback(boolean includeCashback) {
        this.includeCashback = includeCashback;
    }

    public boolean isCashbackPercent() {
        return cashbackPercent;
    }

    public void setCashbackPercent(boolean cashbackPercent) {
        this.cashbackPercent = cashbackPercent;
    }

    public BigDecimal getCashbackValue() {
        return cashbackValue;
    }

    public void setCashbackValue(BigDecimal cashbackValue) {
        this.cashbackValue = cashbackValue;
    }

    public BigDecimal getBenefitEqual() {
        return benefitEqual;
    }

    public void setBenefitEqual(BigDecimal benefitEqual) {
        this.benefitEqual = benefitEqual;
    }

    public BigDecimal getBenefitMin() {
        return benefitMin;
    }

    public void setBenefitMin(BigDecimal benefitMin) {
        this.benefitMin = benefitMin;
    }

    public BigDecimal getBenefitCashback() {
        return benefitCashback;
    }

    public void setBenefitCashback(BigDecimal benefitCashback) {
        this.benefitCashback = benefitCashback;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
