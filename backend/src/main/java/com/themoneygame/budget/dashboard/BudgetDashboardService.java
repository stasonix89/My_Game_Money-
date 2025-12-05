// src/main/java/com/themoneygame/budget/dashboard/BudgetDashboardService.java
package com.themoneygame.budget.dashboard;

import java.time.YearMonth;

public interface BudgetDashboardService {

    BudgetDashboardDto getDashboard(Long userId, YearMonth month);
}
