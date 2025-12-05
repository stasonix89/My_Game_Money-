package com.themoneygame.investments.inflation;

import com.themoneygame.auth.domain.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "capital_inflation_scenarios")
public class CapitalInflationScenario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal initialCapital;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal monthlyContribution;

    @Column(nullable = false)
    private Integer years;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal averageReturn;         // % годовых

    @Column(nullable = false)
    private boolean compareWithDeposit;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal depositRate;           // % годовых

    @Column(nullable = false)
    private boolean useInflation;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal inflationRate;         // % годовых

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false, length = 2000)
    private String resultText;

    // --- геттеры / сеттеры ---

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getInitialCapital() {
        return initialCapital;
    }

    public void setInitialCapital(BigDecimal initialCapital) {
        this.initialCapital = initialCapital;
    }

    public BigDecimal getMonthlyContribution() {
        return monthlyContribution;
    }

    public void setMonthlyContribution(BigDecimal monthlyContribution) {
        this.monthlyContribution = monthlyContribution;
    }

    public Integer getYears() {
        return years;
    }

    public void setYears(Integer years) {
        this.years = years;
    }

    public BigDecimal getAverageReturn() {
        return averageReturn;
    }

    public void setAverageReturn(BigDecimal averageReturn) {
        this.averageReturn = averageReturn;
    }

    public boolean isCompareWithDeposit() {
        return compareWithDeposit;
    }

    public void setCompareWithDeposit(boolean compareWithDeposit) {
        this.compareWithDeposit = compareWithDeposit;
    }

    public BigDecimal getDepositRate() {
        return depositRate;
    }

    public void setDepositRate(BigDecimal depositRate) {
        this.depositRate = depositRate;
    }

    public boolean isUseInflation() {
        return useInflation;
    }

    public void setUseInflation(boolean useInflation) {
        this.useInflation = useInflation;
    }

    public BigDecimal getInflationRate() {
        return inflationRate;
    }

    public void setInflationRate(BigDecimal inflationRate) {
        this.inflationRate = inflationRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }
}
