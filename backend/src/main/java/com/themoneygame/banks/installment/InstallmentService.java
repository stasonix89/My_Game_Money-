package com.themoneygame.banks.installment;

import com.themoneygame.banks.installment.dto.InstallmentRequest;
import com.themoneygame.banks.installment.dto.InstallmentResponse;

import java.util.List;

public interface InstallmentService {

    InstallmentResponse calculateAndSave(Long userId, InstallmentRequest request);

    List<InstallmentResponse> getHistory(Long userId);

    void delete(Long userId, Long calculationId);
}
