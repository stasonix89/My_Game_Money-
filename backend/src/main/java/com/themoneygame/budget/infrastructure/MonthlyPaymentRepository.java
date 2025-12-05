// src/main/java/com/themoneygame/budget/infrastructure/MonthlyPaymentRepository.java
package com.themoneygame.budget.infrastructure;

import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.MonthlyPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MonthlyPaymentRepository extends JpaRepository<MonthlyPayment, Long> {

    // Для сервисов, которые работают с объектом User
    List<MonthlyPayment> findAllByUser(User user);

    List<MonthlyPayment> findAllByUserAndPaymentDateBetween(
            User user,
            LocalDate start,
            LocalDate end
    );

    // Для дэшборда, который работает по userId
    List<MonthlyPayment> findAllByUserIdAndPaymentDateBetween(
            Long userId,
            LocalDate start,
            LocalDate end
    );
}
