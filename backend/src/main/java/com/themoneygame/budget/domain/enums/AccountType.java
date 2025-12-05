// src/main/java/com/themoneygame/budget/domain/enums/AccountType.java
package com.themoneygame.budget.domain.enums;

public enum AccountType {

    /**
     * Обычная дебетовая карта / счёт.
     */
    DEBIT,

    /**
     * Кредитка, предназначенная для покупок (расходы, рассрочки).
     */
    CREDIT_PURCHASE,

    /**
     * Кредитка, с которой удобно снимать наличные (учёт лимита для снятия).
     */
    CREDIT_CASH,

    /**
     * Инвестиционные / накопительные активы (брокерский счёт, ИИС и т.п.).
     */
    ASSET
}
