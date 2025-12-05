package com.themoneygame.banks.installment;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.banks.installment.dto.BenefitBars;
import com.themoneygame.banks.installment.dto.InstallmentRequest;
import com.themoneygame.banks.installment.dto.InstallmentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstallmentServiceImpl implements InstallmentService {

    private final InstallmentCalculationRepository repo;
    private final UserRepository userRepo;

    public InstallmentServiceImpl(InstallmentCalculationRepository repo,
                                  UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Override
    public InstallmentResponse calculateAndSave(Long userId, InstallmentRequest r) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        // безопасные значения по умолчанию
        BigDecimal amount       = nvl(r.getPurchaseAmount(), BigDecimal.ZERO);
        int months              = r.getMonths() != null ? r.getMonths() : 1;
        BigDecimal depositRate  = nvl(r.getDepositRate(), new BigDecimal("8.0")); // % годовых

        boolean includeCashback = r.isIncludeCashback();
        boolean cashbackPercent = r.isCashbackPercent();
        BigDecimal cashbackVal  = nvl(r.getCashbackValue(), BigDecimal.ZERO);

        // годовая ставка во вклад → месячная
        BigDecimal yearlyRate   = depositRate.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP);
        BigDecimal monthlyRate  = yearlyRate.divide(BigDecimal.valueOf(12), 8, RoundingMode.HALF_UP);

        // ----- Равные платежи -----
        BigDecimal monthsBD = BigDecimal.valueOf(months);
        BigDecimal monthlyEqualPayment = amount.divide(monthsBD, 2, RoundingMode.HALF_UP);

        BigDecimal profitEqual = calcDepositProfit(amount, monthlyEqualPayment, months, monthlyRate);

        // ----- Минимальные платежи (условно 10% от суммы в месяц) -----
        BigDecimal minPayment = amount.multiply(new BigDecimal("0.10"))
                .setScale(2, RoundingMode.HALF_UP);
        if (minPayment.compareTo(BigDecimal.ONE) < 0) {
            minPayment = BigDecimal.ONE;
        }

        BigDecimal profitMin = calcDepositProfit(amount, minPayment, months, monthlyRate);

        // ----- Кешбэк -----
        BigDecimal cashback;
        if (cashbackPercent) {
            cashback = amount.multiply(cashbackVal)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            cashback = cashbackVal.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal cashbackBenefit = cashback;

        // если пользователь захотел учитывать кешбэк в выгоде
        if (includeCashback) {
            profitEqual = profitEqual.add(cashbackBenefit);
            profitMin   = profitMin.add(cashbackBenefit);
        }

        // сохраняем сущность
        InstallmentCalculation c = new InstallmentCalculation();
        c.setUser(user);
        c.setPurchaseAmount(amount);
        c.setMonths(months);
        c.setDepositRate(depositRate);
        c.setIncludeCashback(includeCashback);
        c.setCashbackPercent(cashbackPercent);
        c.setCashbackValue(cashbackVal);
        c.setBenefitEqual(profitEqual);
        c.setBenefitMin(profitMin);
        c.setBenefitCashback(cashbackBenefit);

        repo.save(c);

        return toResponse(c);
    }

    /**
     * Моделируем "вклад + рассрочку":
     *  - на вкладе лежит вся сумма покупки
     *  - каждый месяц вклад растёт на monthlyRate
     *  - затем уменьшается на величину платежа
     *  - итоговое значение и есть "выгода" (экономия от вклада)
     */
    private BigDecimal calcDepositProfit(BigDecimal initialAmount,
                                         BigDecimal monthlyPayment,
                                         int months,
                                         BigDecimal monthlyRate) {

        BigDecimal deposit = initialAmount;

        for (int i = 0; i < months; i++) {
            deposit = deposit.multiply(BigDecimal.ONE.add(monthlyRate)); // проценты
            deposit = deposit.subtract(monthlyPayment);                   // платёж
        }

        // выгода не может быть отрицательной
        if (deposit.compareTo(BigDecimal.ZERO) < 0) {
            deposit = BigDecimal.ZERO;
        }
        return deposit.setScale(2, RoundingMode.HALF_UP);
    }

    private InstallmentResponse toResponse(InstallmentCalculation c) {
        BenefitBars bars = new BenefitBars();
        bars.setEqual(c.getBenefitEqual());
        bars.setMin(c.getBenefitMin());
        bars.setCashback(c.getBenefitCashback());

        InstallmentResponse r = new InstallmentResponse();
        r.setId(c.getId());
        r.setCreatedAt(c.getCreatedAt());
        r.setEqualBenefit(c.getBenefitEqual());
        r.setMinBenefit(c.getBenefitMin());
        r.setCashbackBenefit(c.getBenefitCashback());
        r.setBars(bars);

        return r;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstallmentResponse> getHistory(Long userId) {
        return repo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId, Long calculationId) {
        InstallmentCalculation c = repo.findById(calculationId)
                .orElseThrow(() -> new IllegalArgumentException("Calculation not found: " + calculationId));

        if (!c.getUser().getId().equals(userId)) {
            throw new SecurityException("Cannot delete another user's calculation");
        }

        repo.delete(c);
    }

    // helper: null → дефолт
    private BigDecimal nvl(BigDecimal value, BigDecimal def) {
        return value != null ? value : def;
    }
}
