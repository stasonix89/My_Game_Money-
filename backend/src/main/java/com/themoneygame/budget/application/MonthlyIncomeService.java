// src/main/java/com/themoneygame/budget/application/MonthlyIncomeService.java
package com.themoneygame.budget.application;

import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.MonthlyIncome;
import com.themoneygame.budget.infrastructure.MonthlyIncomeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MonthlyIncomeService {

    private final MonthlyIncomeRepository incomeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MonthlyIncomeService(MonthlyIncomeRepository incomeRepository) {
        this.incomeRepository = incomeRepository;
    }

    /**
     * Вся история доходности пользователя.
     */
    public List<MonthlyIncome> getAll(Long userId) {
        return incomeRepository.findAllByUserIdOrderByYearAscMonthAsc(userId);
    }

    /**
     * Доходность за конкретный месяц, либо null.
     */
    public MonthlyIncome getForMonth(Long userId, int year, int month) {
        return incomeRepository
                .findByUserIdAndYearAndMonth(userId, year, month)
                .orElse(null);
    }

    /**
     * Сохранить или обновить запись за месяц:
     * - пересчитывает totalMonthlyIncome
     * - сохраняет extraIncomeMonthly и остальные поля
     */
    @Transactional
    public MonthlyIncome saveOrUpdate(Long userId, MonthlyIncome income) {
        if (income == null) {
            throw new IllegalArgumentException("MonthlyIncome cannot be null");
        }

        // обязательно пересчитать перед сохранением
        income.recalculateTotals();

        return incomeRepository
                .findByUserIdAndYearAndMonth(userId, income.getYear(), income.getMonth())
                .map(existing -> {
                    existing.setYearlyDividends(income.getYearlyDividends());
                    existing.setDepositAmount(income.getDepositAmount());
                    existing.setDepositRateYearly(income.getDepositRateYearly());
                    existing.setSalaryMonthly(income.getSalaryMonthly());
                    existing.setExtraIncomeMonthly(income.getExtraIncomeMonthly());
                    existing.recalculateTotals();
                    return incomeRepository.save(existing);
                })
                .orElseGet(() -> {
                    User userRef = entityManager.getReference(User.class, userId);
                    income.setUser(userRef);
                    income.recalculateTotals();
                    return incomeRepository.save(income);
                });
    }
}
