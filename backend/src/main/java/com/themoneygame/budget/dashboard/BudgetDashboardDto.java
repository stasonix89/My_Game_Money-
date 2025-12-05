// src/main/java/com/themoneygame/budget/dashboard/BudgetDashboardDto.java
package com.themoneygame.budget.dashboard;

import java.math.BigDecimal;
import java.util.List;

public class BudgetDashboardDto {

    private FinanceWidget finance;
    private IncomeWidget income;
    private PaymentsWidget payments;

    public FinanceWidget getFinance() {
        return finance;
    }

    public void setFinance(FinanceWidget finance) {
        this.finance = finance;
    }

    public IncomeWidget getIncome() {
        return income;
    }

    public void setIncome(IncomeWidget income) {
        this.income = income;
    }

    public PaymentsWidget getPayments() {
        return payments;
    }

    public void setPayments(PaymentsWidget payments) {
        this.payments = payments;
    }

    // ----- ВИДЖЕТ УЧЁТА ФИНАНСОВ -----
    public static class FinanceWidget {
        private BigDecimal totalDebitAndAssets;
        private BigDecimal totalCreditForCash;
        private BigDecimal netWorth;

        public BigDecimal getTotalDebitAndAssets() {
            return totalDebitAndAssets;
        }

        public void setTotalDebitAndAssets(BigDecimal totalDebitAndAssets) {
            this.totalDebitAndAssets = totalDebitAndAssets;
        }

        public BigDecimal getTotalCreditForCash() {
            return totalCreditForCash;
        }

        public void setTotalCreditForCash(BigDecimal totalCreditForCash) {
            this.totalCreditForCash = totalCreditForCash;
        }

        public BigDecimal getNetWorth() {
            return netWorth;
        }

        public void setNetWorth(BigDecimal netWorth) {
            this.netWorth = netWorth;
        }
    }

    // ----- ДОХОДНОСТЬ -----
    public static class IncomeWidget {
        private BigDecimal currentMonthIncome;
        private BigDecimal previousMonthIncome;

        public BigDecimal getCurrentMonthIncome() {
            return currentMonthIncome;
        }

        public void setCurrentMonthIncome(BigDecimal currentMonthIncome) {
            this.currentMonthIncome = currentMonthIncome;
        }

        public BigDecimal getPreviousMonthIncome() {
            return previousMonthIncome;
        }

        public void setPreviousMonthIncome(BigDecimal previousMonthIncome) {
            this.previousMonthIncome = previousMonthIncome;
        }
    }

    // ----- МЕСЯЧНЫЕ ПЛАТЕЖИ -----
    public static class PaymentsWidget {
        private List<PaymentRow> rows;
        private BigDecimal totalPlanned;
        private BigDecimal totalPaid;

        public List<PaymentRow> getRows() {
            return rows;
        }

        public void setRows(List<PaymentRow> rows) {
            this.rows = rows;
        }

        public BigDecimal getTotalPlanned() {
            return totalPlanned;
        }

        public void setTotalPlanned(BigDecimal totalPlanned) {
            this.totalPlanned = totalPlanned;
        }

        public BigDecimal getTotalPaid() {
            return totalPaid;
        }

        public void setTotalPaid(BigDecimal totalPaid) {
            this.totalPaid = totalPaid;
        }
    }

    public static class PaymentRow {
        private Long id;
        private String title;
        private BigDecimal amount;
        private boolean paid;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public boolean isPaid() {
            return paid;
        }

        public void setPaid(boolean paid) {
            this.paid = paid;
        }
    }
}
