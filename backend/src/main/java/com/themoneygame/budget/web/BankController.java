// src/main/java/com/themoneygame/budget/web/BankController.java
package com.themoneygame.budget.web;

import com.themoneygame.budget.application.BankService;
import com.themoneygame.budget.domain.Bank;
import com.themoneygame.budget.web.dto.BankDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Эндпоинты для работы со справочником банков.
 *
 * 🔹 GET  /api/budget/banks
 *      → список банков [{id, name}] — фронт показывает в выпадающем списке.
 *
 * 🔹 POST /api/budget/banks
 *      Тело: { "name": "Т-Банк" }
 *      → создаёт банк (если ещё не был) и возвращает его.
 *
 * 🔹 DELETE /api/budget/banks/{id}
 *      → удаление банка из справочника.
 */
@RestController
@RequestMapping("/api/budget/banks")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @GetMapping
    public List<BankDto> getBanks() {
        return bankService.getAllBanks().stream()
                .map(BankDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public BankDto addBank(@RequestBody BankDto dto) {
        Bank bank = bankService.createBank(dto.getName());
        return BankDto.fromEntity(bank);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBank(@PathVariable Long id) {
        bankService.deleteBank(id);
        return ResponseEntity.noContent().build();
    }
}
