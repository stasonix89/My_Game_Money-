// src/main/java/com/themoneygame/budget/web/dto/AccountDto.java
package com.themoneygame.budget.web.dto;

import com.themoneygame.budget.domain.Account;
import com.themoneygame.budget.domain.enums.AccountType;

import java.math.BigDecimal;

/**
 * DTO для счетов/карт/активов.
 *
 * Связан с фронтовым типом:
 *
 * export type AccountType = "DEBIT" | "CREDIT_PURCHASE" | "CREDIT_CASH" | "ASSET";
 *
 * export type AccountDto = {
 *   id: number;
 *   bankName: string;
 *   name: string;
 *   type: AccountType;
 *   limit: number | null;
 *   balance: number;
 *   mainForPayments: boolean;
 *   forWithdraw: boolean;
 * };
 */
public class AccountDto {

    private Long id;
    private String bankName;
    private String name;
    private AccountType type;
    private BigDecimal limit;          // creditLimit в домене
    private BigDecimal balance;
    private boolean mainForPayments;
    private boolean forWithdraw;       // creditForCashWithdrawal в домене

    public AccountDto() {
    }

    public AccountDto(Long id,
                      String bankName,
                      String name,
                      AccountType type,
                      BigDecimal limit,
                      BigDecimal balance,
                      boolean mainForPayments,
                      boolean forWithdraw) {
        this.id = id;
        this.bankName = bankName;
        this.name = name;
        this.type = type;
        this.limit = limit;
        this.balance = balance;
        this.mainForPayments = mainForPayments;
        this.forWithdraw = forWithdraw;
    }

    // ---------- Маппинг из сущности ----------

    public static AccountDto fromEntity(Account account) {
        if (account == null) {
            return null;
        }

        return new AccountDto(
                account.getId(),
                account.getBankName(),
                account.getName(),
                account.getType(),
                account.getCreditLimit(),          // limit
                account.getBalance(),
                account.isMainForPayments(),
                account.isCreditForCashWithdrawal() // forWithdraw
        );
    }

    // ---------- Getters / Setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isMainForPayments() {
        return mainForPayments;
    }

    public void setMainForPayments(boolean mainForPayments) {
        this.mainForPayments = mainForPayments;
    }

    public boolean isForWithdraw() {
        return forWithdraw;
    }

    public void setForWithdraw(boolean forWithdraw) {
        this.forWithdraw = forWithdraw;
    }
}
