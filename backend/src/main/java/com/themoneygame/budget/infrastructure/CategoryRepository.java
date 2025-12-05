// src/main/java/com/themoneygame/budget/infrastructure/CategoryRepository.java
package com.themoneygame.budget.infrastructure;

import com.themoneygame.budget.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);  // Метод для поиска по имени
}
