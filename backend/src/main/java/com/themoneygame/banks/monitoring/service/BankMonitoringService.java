package com.themoneygame.banks.monitoring.service;

import com.themoneygame.banks.monitoring.dto.BankOfferResponse;
import com.themoneygame.banks.monitoring.domain.ProductType;

import java.util.List;

public interface BankMonitoringService {
    List<BankOfferResponse> getOffers(ProductType type);
}
