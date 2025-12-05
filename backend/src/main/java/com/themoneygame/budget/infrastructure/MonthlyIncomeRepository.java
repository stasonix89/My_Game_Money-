package com.themoneygame.budget.infrastructure;

import com.themoneygame.budget.domain.MonthlyIncome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MonthlyIncomeRepository extends JpaRepository<MonthlyIncome, Long> {

    // вся история доходности пользователя
    List<MonthlyIncome> findAllByUserIdOrderByYearAscMonthAsc(Long userId);

    // запись за конкретный месяц
    Optional<MonthlyIncome> findByUserIdAndYearAndMonth(Long userId, int year, int month);
}
