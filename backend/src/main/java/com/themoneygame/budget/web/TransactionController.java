// src/main/java/com/themoneygame/budget/web/TransactionController.java
package com.themoneygame.budget.web;

import com.themoneygame.auth.application.UserDetailsImpl;
import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.Account;
import com.themoneygame.budget.domain.Transaction;
import com.themoneygame.budget.infrastructure.AccountRepository;
import com.themoneygame.budget.infrastructure.TransactionRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/budget")
public class TransactionController {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionController(TransactionRepository transactionRepository,
                                 AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    // ---------- Список транзакций за выбранный месяц ----------

    @GetMapping("/transactions")
    public List<Transaction> listTransactions(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam int year,
            @RequestParam int month
    ) {
        // "лёгкий" User только с id — для репозитория
        User userRef = new User();
        userRef.setId(userDetails.getId());

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // предполагаем, что в репозитории есть метод:
        // List<Transaction> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);
        return transactionRepository.findAllByUserAndDateBetween(userRef, start, end);
    }

    // ---------- Создать новую транзакцию (кнопка "Добавить операцию") ----------

    @PostMapping("/transactions")
    public Transaction createTransaction(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody Transaction tx
    ) {
        // привязываем текущего пользователя
        User userRef = new User();
        userRef.setId(userDetails.getId());
        tx.setUser(userRef);

        // подтягиваем реальный аккаунт по ID из тела запроса
        if (tx.getAccount() != null && tx.getAccount().getId() != null) {
            Account acc = accountRepository.findById(tx.getAccount().getId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));
            tx.setAccount(acc);
        }

        // если дата не пришла — выставляем сегодня
        if (tx.getDate() == null) {
            tx.setDate(LocalDate.now());
        }

        return transactionRepository.save(tx);
    }
}
