package com.themoneygame.budget.dashboard;

import com.themoneygame.budget.domain.Account;
import com.themoneygame.budget.domain.MonthlyIncome;
import com.themoneygame.budget.domain.MonthlyPayment;
import com.themoneygame.budget.infrastructure.AccountRepository;
import com.themoneygame.budget.infrastructure.MonthlyIncomeRepository;
import com.themoneygame.budget.infrastructure.MonthlyPaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BudgetDashboardServiceImpl implements BudgetDashboardService {

    private final AccountRepository accountRepository;
    private final MonthlyIncomeRepository incomeRepository;
    private final MonthlyPaymentRepository paymentRepository;

    public BudgetDashboardServiceImpl(AccountRepository accountRepository,
                                      MonthlyIncomeRepository incomeRepository,
                                      MonthlyPaymentRepository paymentRepository) {
        this.accountRepository = accountRepository;
        this.incomeRepository = incomeRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    public BudgetDashboardDto getDashboard(Long userId, YearMonth month) {
        BudgetDashboardDto dto = new BudgetDashboardDto();

        dto.setFinance(buildFinanceWidget(userId));
        dto.setIncome(buildIncomeWidget(userId, month));
        dto.setPayments(buildPaymentsWidget(userId, month));

        return dto;
    }

    // -----------------------------------------------------
    // FINANCE WIDGET
    // -----------------------------------------------------

    private BudgetDashboardDto.FinanceWidget buildFinanceWidget(Long userId) {
        List<Account> accounts = accountRepository.findByUserId(userId);

        BigDecimal totalDebitAndAssets = accounts.stream()
                .filter(a -> !a.isDebt())
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCreditForCash = accounts.stream()
                .filter(Account::isCreditForCashWithdrawal)
                .map(a -> {
                    // Применяем проверку на null для creditLimit и usedLimit
                    BigDecimal creditLimit = a.getCreditLimit() != null ? a.getCreditLimit() : BigDecimal.ZERO;
                    BigDecimal usedLimit = a.getUsedLimit() != null ? a.getUsedLimit() : BigDecimal.ZERO;

                    // Если использованный лимит равен кредитному лимиту, задолженности нет
                    if (creditLimit.compareTo(usedLimit) == 0) {
                        return BigDecimal.ZERO;
                    }

                    return creditLimit.subtract(usedLimit);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netWorth = totalDebitAndAssets;

        BudgetDashboardDto.FinanceWidget widget = new BudgetDashboardDto.FinanceWidget();
        widget.setTotalDebitAndAssets(totalDebitAndAssets);
        widget.setTotalCreditForCash(totalCreditForCash);
        widget.setNetWorth(netWorth);
        return widget;
    }

    // -----------------------------------------------------
    // INCOME WIDGET
    // -----------------------------------------------------

    private BudgetDashboardDto.IncomeWidget buildIncomeWidget(Long userId, YearMonth month) {
        int year = month.getYear();
        int m = month.getMonthValue();

        MonthlyIncome current = incomeRepository
                .findByUserIdAndYearAndMonth(userId, year, m)
                .orElse(null);

        YearMonth prevMonth = month.minusMonths(1);
        MonthlyIncome previous = incomeRepository
                .findByUserIdAndYearAndMonth(userId, prevMonth.getYear(), prevMonth.getMonthValue())
                .orElse(null);

        BudgetDashboardDto.IncomeWidget widget = new BudgetDashboardDto.IncomeWidget();
        widget.setCurrentMonthIncome(current != null ? current.getTotalMonthlyIncome() : BigDecimal.ZERO);
        widget.setPreviousMonthIncome(previous != null ? previous.getTotalMonthlyIncome() : BigDecimal.ZERO);

        return widget;
    }

    // -----------------------------------------------------
    // PAYMENTS WIDGET
    // -----------------------------------------------------

    private BudgetDashboardDto.PaymentsWidget buildPaymentsWidget(Long userId, YearMonth month) {

        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<MonthlyPayment> payments = paymentRepository
                .findAllByUserIdAndPaymentDateBetween(userId, start, end)
                .stream()
                .sorted(Comparator.comparing(MonthlyPayment::getTitle))
                .toList();

        // rows for UI
        List<BudgetDashboardDto.PaymentRow> rows = payments.stream()
                .map(p -> {
                    BudgetDashboardDto.PaymentRow row = new BudgetDashboardDto.PaymentRow();
                    row.setId(p.getId());
                    row.setTitle(p.getTitle());
                    row.setAmount(p.getAmount());
                    row.setPaid(p.isPaid());
                    return row;
                })
                .toList();

        BigDecimal totalPlanned = payments.stream()
                .map(MonthlyPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaid = payments.stream()
                .filter(MonthlyPayment::isPaid)
                .map(MonthlyPayment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BudgetDashboardDto.PaymentsWidget widget = new BudgetDashboardDto.PaymentsWidget();
        widget.setRows(rows);
        widget.setTotalPlanned(totalPlanned);
        widget.setTotalPaid(totalPaid);

        return widget;
    }
}
