package com.themoneygame.investments.averageprice;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.investments.averageprice.dto.AveragePriceRequest;
import com.themoneygame.investments.averageprice.dto.AveragePriceResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments/average-price")
public class AveragePriceController {

    private final AveragePriceService service;
    private final UserRepository userRepository;

    public AveragePriceController(AveragePriceService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId(UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
        return user.getId();
    }

    /**
     * POST /api/investments/average-price/calculate
     * Выполнить расчёт, сохранить и вернуть результат.
     */
    @PostMapping("/calculate")
    public AveragePriceResponse calculate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AveragePriceRequest request
    ) {
        Long userId = getCurrentUserId(userDetails);
        return service.calculateAndSave(userId, request);
    }

    /**
     * GET /api/investments/average-price/history
     * История сохранённых расчётов текущего пользователя.
     */
    @GetMapping("/history")
    public List<AveragePriceResponse> history(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = getCurrentUserId(userDetails);
        return service.getHistory(userId);
    }

    /**
     * DELETE /api/investments/average-price/{id}
     * Удалить сохранённый расчёт.
     */
    @DeleteMapping("/{id}")
    public void delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        Long userId = getCurrentUserId(userDetails);
        service.deleteCalculation(userId, id);
    }
}
