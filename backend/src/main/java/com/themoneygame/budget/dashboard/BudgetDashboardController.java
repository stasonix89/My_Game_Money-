// src/main/java/com/themoneygame/budget/dashboard/BudgetDashboardController.java
package com.themoneygame.budget.dashboard;

import com.themoneygame.auth.application.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/budget/dashboard")
public class BudgetDashboardController {

    private final BudgetDashboardService dashboardService;

    public BudgetDashboardController(BudgetDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public BudgetDashboardDto getDashboard(
            Authentication authentication,
            @RequestParam(value = "month", required = false) String monthStr
    ) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        YearMonth ym;
        if (monthStr == null || monthStr.isBlank()) {
            ym = YearMonth.now();
        } else {
            ym = YearMonth.parse(monthStr); // формат "yyyy-MM"
        }

        return dashboardService.getDashboard(userId, ym);
    }
}
