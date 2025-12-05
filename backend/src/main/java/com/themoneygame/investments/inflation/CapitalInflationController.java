package com.themoneygame.investments.inflation;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.investments.inflation.dto.CapitalInflationRequest;
import com.themoneygame.investments.inflation.dto.CapitalInflationResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments/inflation")
public class CapitalInflationController {

    private final CapitalInflationService service;
    private final UserRepository userRepo;

    public CapitalInflationController(CapitalInflationService service, UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    private Long getUserId(UserDetails userDetails) {
        String username = userDetails.getUsername();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return user.getId();
    }

    @PostMapping("/calculate")
    public CapitalInflationResponse calculate(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CapitalInflationRequest req
    ) {
        return service.calculateAndSave(getUserId(userDetails), req);
    }

    @GetMapping("/history")
    public List<CapitalInflationResponse> history(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return service.getHistory(getUserId(userDetails));
    }

    @DeleteMapping("/{id}")
    public void delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id
    ) {
        service.deleteScenario(getUserId(userDetails), id);
    }
}
