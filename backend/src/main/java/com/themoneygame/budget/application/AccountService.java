// src/main/java/com/themoneygame/budget/application/AccountService.java
package com.themoneygame.budget.application;

import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.Account;
import com.themoneygame.budget.domain.enums.AccountType;
import com.themoneygame.budget.infrastructure.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final BankService bankService;

    public AccountService(AccountRepository accountRepository,
                          BankService bankService) {
        this.accountRepository = accountRepository;
        this.bankService = bankService;
    }

    public List<Account> getAccountsForUser(User user) {
        return accountRepository.findAllByUser(user);
    }

    @Transactional
    public Account createAccount(User user, Account newAccount) {
        newAccount.setUser(user);

        // гарантируем, что банк попал в справочник
        bankService.ensureBankExists(newAccount.getBankName());

        applyAccountBusinessRules(user, newAccount, null);

        return accountRepository.save(newAccount);
    }

    @Transactional
    public Account updateAccount(User user, Long accountId, Account updatedData) {
        Account existing = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden: account does not belong to current user");
        }

        existing.setBankName(updatedData.getBankName());
        existing.setName(updatedData.getName());
        existing.setType(updatedData.getType());
        existing.setBalance(updatedData.getBalance());
        existing.setCreditLimit(updatedData.getCreditLimit());
        existing.setCreditForCashWithdrawal(updatedData.isCreditForCashWithdrawal());
        existing.setMainForPayments(updatedData.isMainForPayments());

        // банк тоже добавим в справочник, если новый
        bankService.ensureBankExists(existing.getBankName());

        applyAccountBusinessRules(user, existing, existing.getId());

        return accountRepository.save(existing);
    }

    @Transactional
    public void deleteAccount(User user, Long accountId) {
        Account existing = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden: account does not belong to current user");
        }

        accountRepository.delete(existing);
    }

    @Transactional
    public Account setMainForPayments(User user, Long accountId) {
        Account existing = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Forbidden: account does not belong to current user");
        }

        if (existing.getType() != AccountType.DEBIT) {
            throw new RuntimeException("Only debit accounts can be main for payments");
        }

        clearMainForOtherAccounts(user, existing.getId());
        existing.setMainForPayments(true);

        return accountRepository.save(existing);
    }

    // ---------- бизнес-правила ----------

    @Transactional
    protected void applyAccountBusinessRules(User user, Account account, Long selfAccountId) {
        AccountType type = account.getType();

        if (type == null) {
            throw new RuntimeException("Account type must not be null");
        }

        // creditLimit
        switch (type) {
            case DEBIT, ASSET -> account.setCreditLimit(null);
            case CREDIT_PURCHASE, CREDIT_CASH -> { /* ок */ }
        }

        // forWithdraw
        if (type != AccountType.CREDIT_CASH) {
            account.setCreditForCashWithdrawal(false);
        }

        // mainForPayments (только DEBIT)
        if (type == AccountType.DEBIT) {
            if (account.isMainForPayments()) {
                clearMainForOtherAccounts(user, selfAccountId);
            }
        } else {
            account.setMainForPayments(false);
        }
    }

    @Transactional
    protected void clearMainForOtherAccounts(User user, Long exceptAccountId) {
        List<Account> accounts = accountRepository.findAllByUser(user);

        boolean changed = false;
        for (Account acc : accounts) {
            if (acc.isMainForPayments()
                    && (exceptAccountId == null || !acc.getId().equals(exceptAccountId))) {
                acc.setMainForPayments(false);
                changed = true;
            }
        }

        if (changed) {
            accountRepository.saveAll(accounts);
        }
    }
}
