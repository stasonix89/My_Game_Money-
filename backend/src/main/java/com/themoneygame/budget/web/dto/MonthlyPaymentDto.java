package com.themoneygame.budget.web.dto;

import com.themoneygame.budget.domain.MonthlyPayment;
import com.themoneygame.budget.domain.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MonthlyPaymentDto {

    private Long id;
    private String name;
    private String category;  // Теперь это строка, содержащая название категории
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String status;

    public MonthlyPaymentDto() {}

    public MonthlyPaymentDto(Long id, String name, String category, BigDecimal amount, LocalDate paymentDate, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    // Статический метод для преобразования из сущности MonthlyPayment
    public static MonthlyPaymentDto fromEntity(MonthlyPayment payment) {
        if (payment == null) {
            return null;
        }

        // Используем название категории из связанной модели
        String resolvedCategory = (payment.getCategory() != null) ? payment.getCategory().getName() : null;

        // Статус строим из boolean paid.
        String resolvedStatus = payment.isPaid() ? "PAID" : "PLANNED";

        return new MonthlyPaymentDto(
                payment.getId(),
                payment.getTitle(),
                resolvedCategory,  // Здесь маппим название категории
                payment.getAmount(),
                payment.getPaymentDate(),
                resolvedStatus
        );
    }

    // Getters / Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
