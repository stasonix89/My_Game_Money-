package com.themoneygame.investments.averageprice;

import com.themoneygame.auth.domain.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "avg_price_calculations")
public class AveragePriceCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CalculationMode mode;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentAveragePrice;      // текущая средняя

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentQuantity;          // текущее количество бумаг

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal investAmount;             // сумма покупки (X)

    // поля зависят от режима

    @Column(precision = 19, scale = 4)
    private BigDecimal desiredAveragePrice;      // режим 1: желаемая средняя (N)

    @Column(precision = 19, scale = 4)
    private BigDecimal newPrice;                 // режим 2: цена покупки P

    // результаты расчёта (общие для обоих режимов)

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal resultAveragePrice;       // итоговая средняя

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal resultQuantity;           // итоговое количество акций

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal resultTotalAmount;        // общая вложенная сумма (старое + X)

    // для режима 1 дополнительно сохраняем вычисленную цену покупки
    @Column(precision = 19, scale = 4)
    private BigDecimal requiredBuyPrice;         // по какой цене нужно купить на сумму X

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ---- геттеры/сеттеры (можешь сгенерировать в IDEA) ----

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CalculationMode getMode() {
        return mode;
    }

    public void setMode(CalculationMode mode) {
        this.mode = mode;
    }

    public BigDecimal getCurrentAveragePrice() {
        return currentAveragePrice;
    }

    public void setCurrentAveragePrice(BigDecimal currentAveragePrice) {
        this.currentAveragePrice = currentAveragePrice;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public BigDecimal getInvestAmount() {
        return investAmount;
    }

    public void setInvestAmount(BigDecimal investAmount) {
        this.investAmount = investAmount;
    }

    public BigDecimal getDesiredAveragePrice() {
        return desiredAveragePrice;
    }

    public void setDesiredAveragePrice(BigDecimal desiredAveragePrice) {
        this.desiredAveragePrice = desiredAveragePrice;
    }

    public BigDecimal getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(BigDecimal newPrice) {
        this.newPrice = newPrice;
    }

    public BigDecimal getResultAveragePrice() {
        return resultAveragePrice;
    }

    public void setResultAveragePrice(BigDecimal resultAveragePrice) {
        this.resultAveragePrice = resultAveragePrice;
    }

    public BigDecimal getResultQuantity() {
        return resultQuantity;
    }

    public void setResultQuantity(BigDecimal resultQuantity) {
        this.resultQuantity = resultQuantity;
    }

    public BigDecimal getResultTotalAmount() {
        return resultTotalAmount;
    }

    public void setResultTotalAmount(BigDecimal resultTotalAmount) {
        this.resultTotalAmount = resultTotalAmount;
    }

    public BigDecimal getRequiredBuyPrice() {
        return requiredBuyPrice;
    }

    public void setRequiredBuyPrice(BigDecimal requiredBuyPrice) {
        this.requiredBuyPrice = requiredBuyPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
