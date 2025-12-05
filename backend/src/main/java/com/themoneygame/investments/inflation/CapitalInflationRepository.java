package com.themoneygame.investments.inflation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CapitalInflationRepository extends JpaRepository<CapitalInflationScenario, Long> {

    List<CapitalInflationScenario> findByUserIdOrderByCreatedAtDesc(Long userId);
}
