// src/main/java/com/themoneygame/budget/infrastructure/BankRepository.java
package com.themoneygame.budget.infrastructure;

import com.themoneygame.budget.domain.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {

    Optional<Bank> findByNameIgnoreCase(String name);

    List<Bank> findAllByOrderByNameAsc();
}
