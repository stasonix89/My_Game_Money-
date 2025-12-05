package com.themoneygame.investments.inflation;

import com.themoneygame.investments.inflation.dto.CapitalInflationRequest;
import com.themoneygame.investments.inflation.dto.CapitalInflationResponse;

import java.util.List;

public interface CapitalInflationService {

    CapitalInflationResponse calculateAndSave(Long userId, CapitalInflationRequest request);

    List<CapitalInflationResponse> getHistory(Long userId);

    void deleteScenario(Long userId, Long scenarioId);
}
