package com.themoneygame.budget.application;

import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.Account;
import com.themoneygame.budget.domain.MonthlyPayment;
import com.themoneygame.budget.domain.Transaction;
import com.themoneygame.budget.domain.enums.CategoryType;
import com.themoneygame.budget.domain.enums.TransactionType;
import com.themoneygame.budget.infrastructure.AccountRepository;
import com.themoneygame.budget.infrastructure.MonthlyPaymentRepository;
import com.themoneygame.budget.infrastructure.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MonthlyPaymentService {

    private final MonthlyPaymentRepository paymentRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public MonthlyPaymentService(MonthlyPaymentRepository paymentRepository,
                                 AccountRepository accountRepository,
                                 TransactionRepository transactionRepository) {
        this.paymentRepository = paymentRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public void markAsPaid(MonthlyPayment payment, Account primaryDebit) {
        payment.setPaid(true);
        paymentRepository.save(payment);

        Transaction tx = new Transaction(
                payment.getUser(),
                TransactionType.TRANSFER,
                CategoryType.OTHER,  // фиксированная категория для "Погашение обязательств"
                payment.getAmount(),
                primaryDebit,
                payment.getPaymentDate(),
                buildDescriptionForPaymentTransfer(payment, primaryDebit)
        );
        transactionRepository.save(tx);
    }

    @Transactional
    public void unmarkAsPaid(MonthlyPayment payment, Account primaryDebit) {
        payment.setPaid(false);
        paymentRepository.save(payment);

        String prefix = "Погашение обязательств: " + payment.getTitle();
        List<Transaction> allUserTx = transactionRepository.findAllByUser(payment.getUser());

        List<Transaction> toDelete = allUserTx.stream()
                .filter(tx -> tx.getType() == TransactionType.TRANSFER)
                .filter(tx -> tx.getAccount() != null
                        && tx.getAccount().getId().equals(primaryDebit.getId()))
                .filter(tx -> tx.getAmount().compareTo(payment.getAmount()) == 0)
                .filter(tx -> payment.getPaymentDate().equals(tx.getDate()))
                .filter(tx -> tx.getDescription() != null && tx.getDescription().startsWith(prefix))
                .toList();

        if (!toDelete.isEmpty()) {
            transactionRepository.deleteAll(toDelete);
        }
    }

    private String buildDescriptionForPaymentTransfer(MonthlyPayment payment, Account fromAccount) {
        StringBuilder sb = new StringBuilder("Погашение обязательств: ");
        sb.append(payment.getTitle());

        if (payment.getAccount() != null) {
            sb.append(" → на счёт ").append(payment.getAccount().getName());
        }

        if (fromAccount != null) {
            sb.append(" (списание с ").append(fromAccount.getName()).append(")");
        }

        return sb.toString();
    }

    // Новый метод для получения всех платежей пользователя
    public List<MonthlyPayment> getAll(User user) {
        return paymentRepository.findAllByUser(user);
    }

    // Новый метод для сохранения платежа
    @Transactional
    public MonthlyPayment save(MonthlyPayment payment) {
        return paymentRepository.save(payment);
    }

    // Добавляем метод для поиска платежей по месяцу и году
    public List<MonthlyPayment> getByMonth(User user, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.plusMonths(1).minusDays(1);
        return paymentRepository.findAllByUserAndPaymentDateBetween(user, start, end);
    }
}
