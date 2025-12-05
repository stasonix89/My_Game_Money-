package com.themoneygame.banks.monitoring.repository;

import com.themoneygame.banks.monitoring.domain.BankOffer;
import com.themoneygame.banks.monitoring.domain.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankOfferRepository extends JpaRepository<BankOffer, Long> {

    List<BankOffer> findByTypeOrderByUpdatedAtDesc(ProductType type);
}
