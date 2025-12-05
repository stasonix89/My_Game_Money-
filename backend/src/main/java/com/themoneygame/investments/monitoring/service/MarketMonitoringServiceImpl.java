package com.themoneygame.investments.monitoring.service;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.investments.monitoring.domain.*;
import com.themoneygame.investments.monitoring.dto.MarketAssetResponse;
import com.themoneygame.investments.monitoring.dto.WatchlistItemResponse;
import com.themoneygame.investments.monitoring.repository.MarketAssetRepository;
import com.themoneygame.investments.monitoring.repository.UserWatchlistRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarketMonitoringServiceImpl implements MarketMonitoringService {

    private final MarketAssetRepository assetRepo;
    private final UserWatchlistRepository watchlistRepo;
    private final UserRepository userRepo;

    public MarketMonitoringServiceImpl(MarketAssetRepository assetRepo,
                                       UserWatchlistRepository watchlistRepo,
                                       UserRepository userRepo) {
        this.assetRepo = assetRepo;
        this.watchlistRepo = watchlistRepo;
        this.userRepo = userRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarketAssetResponse> getAssetsByType(AssetType type) {
        return assetRepo.findByTypeOrderBySymbolAsc(type).stream()
                .map(this::toAssetResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MarketAssetResponse> getDefaultAssets() {
        // базовый набор тикеров
        List<String> symbols = Arrays.asList(
                "SBERP", "TATNP", "LKOH", // акции
                "GOLD", "SILVER",        // металлы
                "BRENT", "GAZ"           // нефть/газ
        );

        return symbols.stream()
                .map(assetRepo::findBySymbol)
                .filter(java.util.Optional::isPresent)
                .map(opt -> toAssetResponse(opt.get()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WatchlistItemResponse> getWatchlist(Long userId) {
        return watchlistRepo.findByUserIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(this::toWatchlistItem)
                .collect(Collectors.toList());
    }

    @Override
    public WatchlistItemResponse addToWatchlist(Long userId, String symbol, String customName) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        // если уже есть, просто возвращаем существующее
        UserWatchlistEntry existing = watchlistRepo.findByUserIdAndSymbol(userId, symbol)
                .orElse(null);

        if (existing != null) {
            if (customName != null && !customName.isBlank()) {
                existing.setCustomName(customName);
            }
            return toWatchlistItem(existing);
        }

        UserWatchlistEntry entry = new UserWatchlistEntry();
        entry.setUser(user);
        entry.setSymbol(symbol);
        entry.setCustomName(customName);

        watchlistRepo.save(entry);

        return toWatchlistItem(entry);
    }

    @Override
    public void removeFromWatchlist(Long userId, String symbol) {
        watchlistRepo.deleteByUserIdAndSymbol(userId, symbol);
    }

    // ---------- mapping helpers ----------

    private MarketAssetResponse toAssetResponse(MarketAsset a) {
        MarketAssetResponse r = new MarketAssetResponse();
        r.setId(a.getId());
        r.setSymbol(a.getSymbol());
        r.setName(a.getName());
        r.setType(a.getType());
        r.setLastPrice(a.getLastPrice());
        r.setChangeAbsolute(a.getChangeAbsolute());
        r.setChangePercent(a.getChangePercent());
        r.setCurrency(a.getCurrency());
        r.setUpdatedAt(a.getUpdatedAt());
        return r;
    }

    private WatchlistItemResponse toWatchlistItem(UserWatchlistEntry e) {
        WatchlistItemResponse r = new WatchlistItemResponse();
        r.setId(e.getId());
        r.setSymbol(e.getSymbol());

        // пробуем найти человеческое название в MarketAsset
        String name = e.getCustomName();
        if (name == null || name.isBlank()) {
            name = assetRepo.findBySymbol(e.getSymbol())
                    .map(MarketAsset::getName)
                    .orElse(e.getSymbol());
        }
        r.setName(name);
        r.setCreatedAt(e.getCreatedAt());
        return r;
    }
}
