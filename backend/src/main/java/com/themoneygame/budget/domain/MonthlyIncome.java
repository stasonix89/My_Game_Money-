// src/main/java/com/themoneygame/budget/domain/MonthlyIncome.java
package com.themoneygame.budget.domain;

import com.themoneygame.auth.domain.User;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "monthly_income",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "year", "month"})
)
public class MonthlyIncome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private User user;

    @Column(nullable = false)
    private int year;

    @Column(nullable = false)
    private int month; // 1..12

    /**
     * Годовые дивиденды по портфелю, ₽.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal yearlyDividends = BigDecimal.ZERO;

    /**
     * Сумма на вкладе, ₽.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal depositAmount = BigDecimal.ZERO;

    /**
     * Ставка по вкладу, % годовых.
     */
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal depositRateYearly = BigDecimal.ZERO;

    /**
     * Зарплата за месяц, ₽.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal salaryMonthly = BigDecimal.ZERO;

    /**
     * Суммарные дополнительные доходы за месяц, ₽.
     * (детализация по источникам пока на фронте — при необходимости можно
     * сделать отдельную таблицу).
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal extraIncomeMonthly = BigDecimal.ZERO;

    /**
     * Итоговый доход за месяц, ₽.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalMonthlyIncome = BigDecimal.ZERO;

    /**
     * Техническое поле для приёма массива дополнительных доходов с фронта.
     * Не сохраняется в БД, нужно только для JSON → Java.
     *
     * Пример payload:
     *  "extraIncomes": [
     *      { "label": "Фриланс", "amount": 20000 },
     *      { "label": "Аренда",  "amount": 15000 }
     *  ]
     */
    @Transient
    private List<ExtraIncomeItem> extraIncomes = new ArrayList<>();

    // ---- логика перерасчёта ----

    public void recalculateTotals() {
        BigDecimal dividendsMonthly = yearlyDividends
                .divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);

        BigDecimal depositInterestMonthly = depositAmount
                .multiply(depositRateYearly)
                .divide(BigDecimal.valueOf(100), 4, java.math.RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);

        this.totalMonthlyIncome = dividendsMonthly
                .add(depositInterestMonthly)
                .add(salaryMonthly)
                .add(extraIncomeMonthly);
    }

    // ---- getters / setters ----

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public BigDecimal getYearlyDividends() {
        return yearlyDividends;
    }

    public void setYearlyDividends(BigDecimal yearlyDividends) {
        this.yearlyDividends = yearlyDividends != null ? yearlyDividends : BigDecimal.ZERO;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount != null ? depositAmount : BigDecimal.ZERO;
    }

    public BigDecimal getDepositRateYearly() {
        return depositRateYearly;
    }

    public void setDepositRateYearly(BigDecimal depositRateYearly) {
        this.depositRateYearly = depositRateYearly != null ? depositRateYearly : BigDecimal.ZERO;
    }

    public BigDecimal getSalaryMonthly() {
        return salaryMonthly;
    }

    public void setSalaryMonthly(BigDecimal salaryMonthly) {
        this.salaryMonthly = salaryMonthly != null ? salaryMonthly : BigDecimal.ZERO;
    }

    public BigDecimal getExtraIncomeMonthly() {
        return extraIncomeMonthly;
    }

    public void setExtraIncomeMonthly(BigDecimal extraIncomeMonthly) {
        this.extraIncomeMonthly = extraIncomeMonthly != null ? extraIncomeMonthly : BigDecimal.ZERO;
    }

    public BigDecimal getTotalMonthlyIncome() {
        return totalMonthlyIncome != null ? totalMonthlyIncome : BigDecimal.ZERO;
    }

    public void setTotalMonthlyIncome(BigDecimal totalMonthlyIncome) {
        this.totalMonthlyIncome = totalMonthlyIncome;
    }

    public List<ExtraIncomeItem> getExtraIncomes() {
        return extraIncomes;
    }

    public void setExtraIncomes(List<ExtraIncomeItem> extraIncomes) {
        this.extraIncomes = extraIncomes != null ? extraIncomes : new ArrayList<>();
    }

    /**
     * Элемент массива дополнительных доходов (для JSON).
     */
    public static class ExtraIncomeItem {
        private String label;
        private BigDecimal amount;

        public ExtraIncomeItem() {
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }
}
