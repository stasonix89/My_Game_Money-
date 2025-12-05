// src/main/java/com/themoneygame/budget/web/MonthlyIncomeController.java
package com.themoneygame.budget.web;

import com.themoneygame.auth.application.UserDetailsImpl;
import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.budget.application.MonthlyIncomeService;
import com.themoneygame.budget.domain.MonthlyIncome;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Objects;

@RestController
@RequestMapping("/api/budget/income")
public class MonthlyIncomeController {

    private final MonthlyIncomeService incomeService;
    private final UserRepository userRepository;

    public MonthlyIncomeController(MonthlyIncomeService incomeService,
                                   UserRepository userRepository) {
        this.incomeService = incomeService;
        this.userRepository = userRepository;
    }

    // маленький helper: получить текущего User
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }

        Long userId = userDetails.getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found: " + userId));
    }

    // -------- GET: получить доходность за месяц --------
    @GetMapping
    public MonthlyIncome getIncome(@RequestParam int year,
                                   @RequestParam int month) {

        User user = getCurrentUser();

        MonthlyIncome income = incomeService.getForMonth(user.getId(), year, month);
        if (income != null) {
            return income;
        }

        // если записи нет – возвращаем "пустой" объект с нулями,
        // чтобы фронт отобразил текущие значения и мог их сохранить
        MonthlyIncome mi = new MonthlyIncome();
        mi.setUser(user);
        mi.setYear(year);
        mi.setMonth(month);
        mi.recalculateTotals();
        return mi;
    }

    // -------- POST: сохранить / обновить доходность --------
    @PostMapping
    public MonthlyIncome saveIncome(@RequestBody MonthlyIncome incoming) {

        User user = getCurrentUser();

        if (incoming == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body is empty");
        }

        // гарантируем, что год/месяц переданы
        if (incoming.getYear() == 0 || incoming.getMonth() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year and month must be set");
        }

        // 1) Сложить все extraIncomes.amount → получить extraIncomeMonthly
        BigDecimal extras = incoming.getExtraIncomes().stream()
                .map(MonthlyIncome.ExtraIncomeItem::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        incoming.setExtraIncomeMonthly(extras);

        // 2) Сохранить через сервис (там recalculateTotals + апдейт/insert)
        return incomeService.saveOrUpdate(user.getId(), incoming);
    }
}
