// src/main/java/com/themoneygame/budget/infrastructure/AccountRepository.java
package com.themoneygame.budget.infrastructure;

import com.themoneygame.auth.domain.User;
import com.themoneygame.budget.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    // уже было — для контроллеров/сервисов
    List<Account> findAllByUser(User user);

    // нужно для Dashboard, который работает по userId
    List<Account> findByUserId(Long userId);
}
