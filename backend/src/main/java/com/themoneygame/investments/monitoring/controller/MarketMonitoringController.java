package com.themoneygame.investments.monitoring.controller;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.investments.monitoring.domain.AssetType;
import com.themoneygame.investments.monitoring.dto.MarketAssetResponse;
import com.themoneygame.investments.monitoring.dto.WatchlistItemResponse;
import com.themoneygame.investments.monitoring.dto.WatchlistModifyRequest;
import com.themoneygame.investments.monitoring.service.MarketMonitoringService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments/monitoring")
public class MarketMonitoringController {

    private final MarketMonitoringService service;
    private final UserRepository userRepo;

    public MarketMonitoringController(MarketMonitoringService service,
                                      UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    private Long uid(UserDetails ud) {
        User u = userRepo.findByUsername(ud.getUsername()).orElseThrow();
        return u.getId();
    }

    /**
     * GET /api/investments/monitoring/assets?type=STOCK|METAL|COMMODITY
     */
    @GetMapping("/assets")
    public List<MarketAssetResponse> getAssets(@RequestParam AssetType type) {
        return service.getAssetsByType(type);
    }

    /**
     * GET /api/investments/monitoring/default
     *  - базовый набор: SBERP, TATNP, LKOH, GOLD, SILVER, BRENT, GAZ
     */
    @GetMapping("/default")
    public List<MarketAssetResponse> getDefault() {
        return service.getDefaultAssets();
    }

    /**
     * GET /api/investments/monitoring/watchlist
     */
    @GetMapping("/watchlist")
    public List<WatchlistItemResponse> getWatchlist(@AuthenticationPrincipal UserDetails ud) {
        return service.getWatchlist(uid(ud));
    }

    /**
     * POST /api/investments/monitoring/watchlist
     * body: { "symbol": "SBERP", "customName": "Сбер преф" }
     */
    @PostMapping("/watchlist")
    public WatchlistItemResponse addWatchlist(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody WatchlistModifyRequest req
    ) {
        return service.addToWatchlist(uid(ud), req.getSymbol(), req.getCustomName());
    }

    /**
     * DELETE /api/investments/monitoring/watchlist/{symbol}
     */
    @DeleteMapping("/watchlist/{symbol}")
    public void deleteWatchlist(
            @AuthenticationPrincipal UserDetails ud,
            @PathVariable String symbol
    ) {
        service.removeFromWatchlist(uid(ud), symbol);
    }
}
