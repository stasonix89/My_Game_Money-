package com.themoneygame.investments.monitoring.service;

import com.themoneygame.investments.monitoring.domain.AssetType;
import com.themoneygame.investments.monitoring.dto.MarketAssetResponse;
import com.themoneygame.investments.monitoring.dto.WatchlistItemResponse;

import java.util.List;

public interface MarketMonitoringService {

    /**
     * Получить список активов по типу (акции / металлы / товары).
     */
    List<MarketAssetResponse> getAssetsByType(AssetType type);

    /**
     * Дефолтный набор для стартовой страницы:
     * Сбер-п, Татнефть-п, Лукойл, золото, серебро, нефть, газ.
     */
    List<MarketAssetResponse> getDefaultAssets();

    /**
     * Вотчлист текущего пользователя.
     */
    List<WatchlistItemResponse> getWatchlist(Long userId);

    /**
     * Добавить актив в вотчлист (если не был добавлен).
     */
    WatchlistItemResponse addToWatchlist(Long userId, String symbol, String customName);

    /**
     * Удалить актив из вотчлиста.
     */
    void removeFromWatchlist(Long userId, String symbol);
}
