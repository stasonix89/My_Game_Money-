package com.themoneygame.investments.inflation;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.investments.inflation.dto.CapitalInflationRequest;
import com.themoneygame.investments.inflation.dto.CapitalInflationResponse;
import com.themoneygame.investments.inflation.dto.CapitalInflationYearPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class CapitalInflationServiceImpl implements CapitalInflationService {

    private final CapitalInflationRepository repo;
    private final UserRepository userRepo;

    public CapitalInflationServiceImpl(CapitalInflationRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Override
    public CapitalInflationResponse calculateAndSave(Long userId, CapitalInflationRequest req) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found " + userId));

        // безопасные значения по умолчанию (как в ТЗ: 9% инфляция, 8% вклад)
        BigDecimal initialCapital = nvl(req.getInitialCapital(), BigDecimal.ZERO);
        BigDecimal monthlyContribution = nvl(req.getMonthlyContribution(), BigDecimal.ZERO);
        int years = req.getYears() != null ? req.getYears() : 1;

        BigDecimal averageReturn = nvl(req.getAverageReturn(), BigDecimal.ZERO);        // %
        BigDecimal depositRate   = nvl(req.getDepositRate(), new BigDecimal("8.0"));   // %
        BigDecimal inflationRate = nvl(req.getInflationRate(), new BigDecimal("9.0")); // %

        boolean compareWithDeposit = req.isCompareWithDeposit();
        boolean useInflation       = req.isUseInflation();

        // состояние капиталов
        BigDecimal investValue        = initialCapital;
        BigDecimal depositValue       = initialCapital;
        BigDecimal inflationAdjusted  = initialCapital;

        // месячные коэффициенты
        BigDecimal monthlyReturnRate   = averageReturn.divide(BigDecimal.valueOf(12 * 100L), 8, RoundingMode.HALF_UP);
        BigDecimal monthlyDepositRate  = depositRate.divide(BigDecimal.valueOf(12 * 100L), 8, RoundingMode.HALF_UP);
        BigDecimal monthlyInflationRate= inflationRate.divide(BigDecimal.valueOf(12 * 100L), 8, RoundingMode.HALF_UP);

        List<CapitalInflationYearPoint> points = new ArrayList<>();
        int totalMonths = years * 12;

        for (int month = 1; month <= totalMonths; month++) {

            // 1) Инвестпортфель: добавляем взнос + применяем доходность
            investValue = investValue.add(monthlyContribution);
            investValue = investValue.multiply(BigDecimal.ONE.add(monthlyReturnRate));

            // 2) Вклад: если включен режим сравнения, то считаем аналогично, только с депозитной ставкой
            if (compareWithDeposit) {
                depositValue = depositValue.add(monthlyContribution);
                depositValue = depositValue.multiply(BigDecimal.ONE.add(monthlyDepositRate));
            }

            // 3) Корректировка капитала на инфляцию (условно "реальная покупательная способность")
            if (useInflation) {
                inflationAdjusted = inflationAdjusted.multiply(BigDecimal.ONE.add(monthlyInflationRate.negate()));
            }

            // 4) Раз в год записываем точку для графика
            if (month % 12 == 0) {
                CapitalInflationYearPoint p = new CapitalInflationYearPoint();
                p.setYear(month / 12);
                p.setInvestmentValue(investValue);
                p.setDepositValue(compareWithDeposit ? depositValue : null);
                p.setInflationAdjusted(useInflation ? inflationAdjusted : null);
                points.add(p);
            }
        }

        // Текстовый итог (чтобы фронт показал готовую формулировку)
        String resultText = buildResultText(years, investValue, compareWithDeposit ? depositValue : null,
                useInflation ? inflationAdjusted : null);

        // Сохраняем сценарий в БД
        CapitalInflationScenario entity = new CapitalInflationScenario();
        entity.setUser(user);
        entity.setInitialCapital(initialCapital);
        entity.setMonthlyContribution(monthlyContribution);
        entity.setYears(years);
        entity.setAverageReturn(averageReturn);
        entity.setCompareWithDeposit(compareWithDeposit);
        entity.setDepositRate(depositRate);
        entity.setUseInflation(useInflation);
        entity.setInflationRate(inflationRate);
        entity.setResultText(resultText);

        repo.save(entity);

        // Формируем ответ
        CapitalInflationResponse resp = new CapitalInflationResponse();
        resp.setId(entity.getId());
        resp.setCreatedAt(entity.getCreatedAt());
        resp.setResultText(resultText);
        resp.setGraph(points);

        return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CapitalInflationResponse> getHistory(Long userId) {
        List<CapitalInflationScenario> list = repo.findByUserIdOrderByCreatedAtDesc(userId);

        List<CapitalInflationResponse> out = new ArrayList<>();
        for (CapitalInflationScenario e : list) {
            CapitalInflationResponse r = new CapitalInflationResponse();
            r.setId(e.getId());
            r.setCreatedAt(e.getCreatedAt());
            r.setResultText(e.getResultText());
            r.setGraph(Collections.emptyList()); // для списка график не тянем
            out.add(r);
        }
        return out;
    }

    @Override
    public void deleteScenario(Long userId, Long scenarioId) {
        CapitalInflationScenario e = repo.findById(scenarioId)
                .orElseThrow(() -> new IllegalArgumentException("Scenario not found: " + scenarioId));

        if (!e.getUser().getId().equals(userId)) {
            throw new SecurityException("Cannot delete scenario of another user");
        }

        repo.delete(e);
    }

    // --------- private helpers ---------

    private BigDecimal nvl(BigDecimal value, BigDecimal def) {
        return value != null ? value : def;
    }

    private String buildResultText(int years,
                                   BigDecimal investValue,
                                   BigDecimal depositValue,
                                   BigDecimal inflationAdjusted) {

        StringBuilder sb = new StringBuilder();
        sb.append("Через ").append(years).append(" лет капитал в инвестициях составит ")
                .append(format(investValue)).append(" ₽.");

        if (depositValue != null) {
            sb.append(" На вкладе за тот же период: ").append(format(depositValue)).append(" ₽.");
        }

        if (inflationAdjusted != null) {
            sb.append(" Реальная стоимость капитала с учётом инфляции: ")
                    .append(format(inflationAdjusted)).append(" ₽.");
        }

        return sb.toString();
    }

    private String format(BigDecimal value) {
        if (value == null) return "—";
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
