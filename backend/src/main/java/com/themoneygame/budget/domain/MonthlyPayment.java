package com.themoneygame.budget.domain;

import com.themoneygame.auth.domain.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "monthly_payments")
public class MonthlyPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Account account;     // счёт, с которого платится обязательство

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate paymentDate;   // реальная дата обязательства (когда оно должно быть оплачено)

    @Column(nullable = false)
    private boolean paid = false;

    @ManyToOne(fetch = FetchType.LAZY) // Новая связь с категорией
    @JoinColumn(name = "category_id")
    private Category category; // Добавляем поле для категории

    public MonthlyPayment() {
    }

    public MonthlyPayment(
            User user,
            Account account,
            LocalDate paymentDate,
            BigDecimal amount,
            String title,
            Category category // Добавляем категорию в конструктор
    ) {
        this.user = user;
        this.account = account;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.title = title;
        this.paid = false;
        this.category = category; // Устанавливаем категорию
    }

    // =======================
    // Getters / Setters
    // =======================

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount != null ? amount : BigDecimal.ZERO;
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

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Category getCategory() { // Геттер для категории
        return category;
    }

    public void setCategory(Category category) { // Сеттер для категории
        this.category = category;
    }
}
