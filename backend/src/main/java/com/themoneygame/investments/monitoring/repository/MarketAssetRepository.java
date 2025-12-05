package com.themoneygame.investments.monitoring.repository;

import com.themoneygame.investments.monitoring.domain.AssetType;
import com.themoneygame.investments.monitoring.domain.MarketAsset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MarketAssetRepository extends JpaRepository<MarketAsset, Long> {

    List<MarketAsset> findByTypeOrderBySymbolAsc(AssetType type);

    Optional<MarketAsset> findBySymbol(String symbol);
}
