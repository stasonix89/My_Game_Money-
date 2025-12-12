// src/main/java/com/themoneygame/budget/web/BudgetController.java
package com.themoneygame.budget.web;

import com.themoneygame.auth.application.UserDetailsImpl;
import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.application.AccountService;
import com.themoneygame.budget.application.TransactionService;
import com.themoneygame.budget.domain.Account;
import com.themoneygame.budget.web.dto.AccountDto;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.apache.log4j.LogManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {


    private static final Logger loger = LogManager.getLogger(BudgetController.class);
    private final AccountService accountService;
    private final TransactionService transactionService;

    public BudgetController(AccountService accountService,
                            TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private User toUser(UserDetailsImpl userDetails) {
        return userDetails.toUser();
    }

    private AccountDto toDto(Account account) {
        return AccountDto.fromEntity(account);
    }

    private Account toAccountEntity(AccountDto dto) {
        Account acc = new Account();
        acc.setBankName(dto.getBankName());
        acc.setName(dto.getName());
        acc.setType(dto.getType());
        acc.setBalance(dto.getBalance());
        acc.setCreditLimit(dto.getLimit());
        acc.setMainForPayments(dto.isMainForPayments());
        acc.setCreditForCashWithdrawal(dto.isForWithdraw());
        return acc;
    }

    // -------------------------------------------------------
    // ACCOUNTS CRUD
    // -------------------------------------------------------

    /**
     * Получить все счета пользователя.
     *
     * Ответ: AccountDto[] (только текущий пользователь).
     */
    @GetMapping("/accounts")
    public List<AccountDto> getAccounts(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User userRef = toUser(userDetails);
        loger.debug(String.format("1: %s", userRef.toString()));
        return accountService.getAccountsForUser(userRef).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Создать новый счёт / карту / актив.
     *
     * Тело запроса:
     * {
     *   "bankName": "Т-банк",
     *   "name": "Т-дебет *0014",
     *   "type": "DEBIT",
     *   "limit": null,
     *   "balance": 85000.00,
     *   "mainForPayments": false,
     *   "forWithdraw": false
     * }
     *
     * user берётся из Security.
     * Все проверки и правки по type/limit/forWithdraw/mainForPayments
     * выполняются в AccountService.applyAccountBusinessRules(...).
     */
    @PostMapping("/accounts")
    public AccountDto addAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody AccountDto dto
    ) {
        User userRef = toUser(userDetails);

        Account newAccount = toAccountEntity(dto);
        Account saved = accountService.createAccount(userRef, newAccount);

        return toDto(saved);
    }

    /**
     * Обновить существующий счёт.
     *
     * Тело запроса — те же поля, что и в POST.
     * Валидация и нормализация флагов/лимитов — в AccountService.
     */
    @PutMapping("/accounts/{id}")
    public AccountDto updateAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @RequestBody AccountDto dto
    ) {
        User userRef = toUser(userDetails);

        Account updatedData = toAccountEntity(dto);
        Account saved = accountService.updateAccount(userRef, id, updatedData);

        return toDto(saved);
    }

    /**
     * Удалить счёт.
     *
     * Сейчас удаление происходит без проверки существующих транзакций.
     * В будущем можно добавить поведение:
     *  - 400 ACCOUNT_HAS_TRANSACTIONS, если есть связанные транзакции.
     */
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<?> deleteAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        User userRef = toUser(userDetails);
        accountService.deleteAccount(userRef, id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------
    // MAIN FOR PAYMENTS
    // -------------------------------------------------------

    /**
     * Основная карта для списания обязательных платежей.
     *
     * ✔ 200 OK — если основная карта найдена
     * ✔ 404 NOT FOUND — если основной карты нет
     */
    @GetMapping("/accounts/main-for-payments")
    public ResponseEntity<?> getMainAccountForPayments(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User userRef = toUser(userDetails);

        return accountService.getAccountsForUser(userRef).stream()
                .filter(Account::isMainForPayments)
                .findFirst()
                .<ResponseEntity<?>>map(acc -> ResponseEntity.ok(toDto(acc)))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "error", "MAIN_PAYMENT_ACCOUNT_NOT_FOUND",
                                "message", "Primary payment account not found"
                        )));
    }

    /**
     * Отметить счёт как основной для списаний.
     *
     * Важно:
     *  - Разрешено только для DEBIT (проверяется в AccountService.setMainForPayments).
     *  - У всех других аккаунтов пользователя mainForPayments = false.
     */
    @PostMapping("/accounts/{id}/set-main-for-payments")
    public AccountDto setMainForPayments(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        User userRef = toUser(userDetails);
        Account updated = accountService.setMainForPayments(userRef, id);
        return toDto(updated);
    }
}
