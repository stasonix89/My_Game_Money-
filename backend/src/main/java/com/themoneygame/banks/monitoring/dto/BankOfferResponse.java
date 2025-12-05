package com.themoneygame.banks.monitoring.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Setter
@Getter
public class BankOfferResponse {
    private Long id;
    private String bankName;
    private String title;
    private String description;

    private BigDecimal interestRate;
    private BigDecimal cashback;
    private BigDecimal limitAmount;
    private Integer gracePeriod;
    private BigDecimal minDeposit;
    private LocalDateTime updatedAt;

    // getters/setters
}
