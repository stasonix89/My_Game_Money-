package com.themoneygame.banks.monitoring.controller;

import com.themoneygame.banks.monitoring.domain.ProductType;
import com.themoneygame.banks.monitoring.dto.BankOfferResponse;
import com.themoneygame.banks.monitoring.service.BankMonitoringService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks/monitoring")
public class BankMonitoringController {

    private final BankMonitoringService service;

    public BankMonitoringController(BankMonitoringService service) {
        this.service = service;
    }

    @GetMapping
    public List<BankOfferResponse> getOffers(@RequestParam ProductType type) {
        return service.getOffers(type);
    }
}
