package com.themoneygame.investments.averageprice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AveragePriceCalculationRepository extends JpaRepository<AveragePriceCalculation, Long> {

    List<AveragePriceCalculation> findByUserIdOrderByCreatedAtDesc(Long userId);
}
