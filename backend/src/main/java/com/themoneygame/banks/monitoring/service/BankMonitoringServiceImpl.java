package com.themoneygame.banks.monitoring.service;

import com.themoneygame.banks.monitoring.domain.BankOffer;
import com.themoneygame.banks.monitoring.domain.ProductType;
import com.themoneygame.banks.monitoring.dto.BankOfferResponse;
import com.themoneygame.banks.monitoring.repository.BankOfferRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankMonitoringServiceImpl implements BankMonitoringService {

    private final BankOfferRepository repo;

    public BankMonitoringServiceImpl(BankOfferRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<BankOfferResponse> getOffers(ProductType type) {

        return repo.findByTypeOrderByUpdatedAtDesc(type)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private BankOfferResponse toResponse(BankOffer o) {

        BankOfferResponse r = new BankOfferResponse();
        r.setId(o.getId());
        r.setBankName(o.getBankName());
        r.setTitle(o.getTitle());
        r.setDescription(o.getDescription());
        r.setInterestRate(o.getInterestRate());
        r.setCashback(o.getCashback());
        r.setLimitAmount(o.getLimitAmount());
        r.setGracePeriod(o.getGracePeriod());
        r.setMinDeposit(o.getMinDeposit());
        r.setUpdatedAt(o.getUpdatedAt());

        return r;
    }
}
