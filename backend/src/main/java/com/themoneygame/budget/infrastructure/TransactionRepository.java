package com.themoneygame.budget.infrastructure;

import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByUser(User user);

    List<Transaction> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}
