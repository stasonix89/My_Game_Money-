// src/main/java/com/themoneygame/budget/application/BankService.java
package com.themoneygame.budget.application;

import com.themoneygame.budget.domain.Bank;
import com.themoneygame.budget.infrastructure.BankRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BankService {

    private final BankRepository bankRepository;

    public BankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Transactional(readOnly = true)
    public List<Bank> getAllBanks() {
        return bankRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public Bank createBank(String name) {
        return bankRepository
                .findByNameIgnoreCase(name.trim())
                .orElseGet(() -> bankRepository.save(new Bank(name.trim())));
    }

    @Transactional
    public void deleteBank(Long id) {
        bankRepository.deleteById(id);
    }

    /**
     * Используем из AccountService:
     * гарантируем, что банк с таким именем есть в справочнике.
     */
    @Transactional
    public void ensureBankExists(String name) {
        if (name == null || name.isBlank()) {
            return;
        }
        createBank(name);
    }
}
