package com.themoneygame.investments.averageprice;

import com.themoneygame.investments.averageprice.dto.AveragePriceRequest;
import com.themoneygame.investments.averageprice.dto.AveragePriceResponse;

import java.util.List;

public interface AveragePriceService {

    /**
     * Выполнить расчёт, сохранить результат в БД и вернуть его.
     */
    AveragePriceResponse calculateAndSave(Long userId, AveragePriceRequest request);

    /**
     * История расчётов текущего пользователя.
     */
    List<AveragePriceResponse> getHistory(Long userId);

    /**
     * Удалить сохранённый расчёт (только свой).
     */
    void deleteCalculation(Long userId, Long calculationId);
}
