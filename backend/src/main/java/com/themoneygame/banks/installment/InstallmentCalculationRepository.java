package com.themoneygame.banks.installment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InstallmentCalculationRepository
        extends JpaRepository<InstallmentCalculation, Long> {

    List<InstallmentCalculation> findByUserIdOrderByCreatedAtDesc(Long userId);
}
