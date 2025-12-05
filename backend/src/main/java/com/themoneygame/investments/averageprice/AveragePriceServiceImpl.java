package com.themoneygame.investments.averageprice;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.investments.averageprice.dto.AveragePriceRequest;
import com.themoneygame.investments.averageprice.dto.AveragePriceResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AveragePriceServiceImpl implements AveragePriceService {

    private final AveragePriceCalculationRepository repository;
    private final UserRepository userRepository;

    public AveragePriceServiceImpl(AveragePriceCalculationRepository repository,
                                   UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public AveragePriceResponse calculateAndSave(Long userId, AveragePriceRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        AveragePriceCalculation entity = new AveragePriceCalculation();
        entity.setUser(user);
        entity.setMode(req.getMode());
        entity.setCurrentAveragePrice(req.getCurrentAveragePrice());
        entity.setCurrentQuantity(req.getCurrentQuantity());
        entity.setInvestAmount(req.getInvestAmount());
        entity.setDesiredAveragePrice(req.getDesiredAveragePrice());
        entity.setNewPrice(req.getNewPrice());

        // математика
        if (req.getMode() == CalculationMode.TARGET_AVERAGE) {
            calculateTargetAverage(entity);
        } else if (req.getMode() == CalculationMode.NEW_AVERAGE) {
            calculateNewAverage(entity);
        } else {
            throw new IllegalArgumentException("Unknown mode: " + req.getMode());
        }

        repository.save(entity);
        return toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AveragePriceResponse> getHistory(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCalculation(Long userId, Long calculationId) {
        AveragePriceCalculation entity = repository.findById(calculationId)
                .orElseThrow(() -> new IllegalArgumentException("Calculation not found: " + calculationId));

        if (!entity.getUser().getId().equals(userId)) {
            throw new SecurityException("Cannot delete calculation of another user");
        }

        repository.delete(entity);
    }

    // ---------- математика ----------

    /**
     * Режим 1: известна желаемая средняя цена (P*), есть сумма X,
     * нужно посчитать цену покупки Pbuy, чтобы при покупке на X
     * средняя стала P*.
     *
     * Формула:
     *   C0 = P0 * Q0  (старые вложения)
     *   P* = (C0 + X) / (Q0 + X / Pbuy)
     *   → Pbuy = P* * X / (C0 + X - P* * Q0)
     */
    private void calculateTargetAverage(AveragePriceCalculation e) {
        BigDecimal P0 = e.getCurrentAveragePrice();
        BigDecimal Q0 = e.getCurrentQuantity();
        BigDecimal X  = e.getInvestAmount();
        BigDecimal Pstar = e.getDesiredAveragePrice();

        BigDecimal C0 = P0.multiply(Q0);                // старые вложения
        BigDecimal numerator = Pstar.multiply(X);       // P* * X
        BigDecimal denominator = C0.add(X).subtract(Pstar.multiply(Q0)); // C0 + X - P*Q0

        if (denominator.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Невозможно достичь такой средней ценой при заданной сумме");
        }

        BigDecimal Pbuy = numerator.divide(denominator, 4, RoundingMode.HALF_UP);

        // сколько акций купим
        BigDecimal Q1 = X.divide(Pbuy, 4, RoundingMode.HALF_UP);

        BigDecimal newQuantity = Q0.add(Q1);
        BigDecimal totalAmount = C0.add(X);

        e.setRequiredBuyPrice(Pbuy);
        e.setResultAveragePrice(Pstar);
        e.setResultQuantity(newQuantity);
        e.setResultTotalAmount(totalAmount);
    }

    /**
     * Режим 2: покупаем по цене Pbuy на сумму X, считаем новую среднюю.
     *
     * Формула:
     *   C0 = P0 * Q0
     *   Q1 = X / Pbuy
     *   Pnew = (C0 + X) / (Q0 + Q1)
     */
    private void calculateNewAverage(AveragePriceCalculation e) {
        BigDecimal P0 = e.getCurrentAveragePrice();
        BigDecimal Q0 = e.getCurrentQuantity();
        BigDecimal X  = e.getInvestAmount();
        BigDecimal Pbuy = e.getNewPrice();

        BigDecimal C0 = P0.multiply(Q0);
        BigDecimal Q1 = X.divide(Pbuy, 4, RoundingMode.HALF_UP);

        BigDecimal newQuantity = Q0.add(Q1);
        BigDecimal totalAmount = C0.add(X);
        BigDecimal Pnew = totalAmount.divide(newQuantity, 4, RoundingMode.HALF_UP);

        e.setRequiredBuyPrice(null); // в этом режиме не нужен
        e.setResultAveragePrice(Pnew);
        e.setResultQuantity(newQuantity);
        e.setResultTotalAmount(totalAmount);
    }

    // ---------- маппинг в DTO ----------

    private AveragePriceResponse toResponse(AveragePriceCalculation e) {
        AveragePriceResponse dto = new AveragePriceResponse();
        dto.setId(e.getId());
        dto.setMode(e.getMode());
        dto.setCurrentAveragePrice(e.getCurrentAveragePrice());
        dto.setCurrentQuantity(e.getCurrentQuantity());
        dto.setInvestAmount(e.getInvestAmount());
        dto.setDesiredAveragePrice(e.getDesiredAveragePrice());
        dto.setNewPrice(e.getNewPrice());
        dto.setResultAveragePrice(e.getResultAveragePrice());
        dto.setResultQuantity(e.getResultQuantity());
        dto.setResultTotalAmount(e.getResultTotalAmount());
        dto.setRequiredBuyPrice(e.getRequiredBuyPrice());
        dto.setCreatedAt(e.getCreatedAt());
        return dto;
    }
}
