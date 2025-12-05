package com.themoneygame.investments.inflation.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Ответ на запрос расчёта / сценария.
 * Включает текстовый итог + точки для графика.
 */
public class CapitalInflationResponse {

    private Long id;
    private LocalDateTime createdAt;

    private String resultText;

    private List<CapitalInflationYearPoint> graph;

    // --- геттеры / сеттеры ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public List<CapitalInflationYearPoint> getGraph() {
        return graph;
    }

    public void setGraph(List<CapitalInflationYearPoint> graph) {
        this.graph = graph;
    }
}
