package com.themoneygame.banks.installment;

import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import com.themoneygame.banks.installment.dto.InstallmentRequest;
import com.themoneygame.banks.installment.dto.InstallmentResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banks/installment")
public class InstallmentController {

    private final InstallmentService service;
    private final UserRepository userRepo;

    public InstallmentController(InstallmentService service,
                                 UserRepository userRepo) {
        this.service = service;
        this.userRepo = userRepo;
    }

    private Long getUserId(UserDetails ud) {
        User u = userRepo.findByUsername(ud.getUsername())
                .orElseThrow();
        return u.getId();
    }

    @PostMapping("/calculate")
    public InstallmentResponse calculate(
            @AuthenticationPrincipal UserDetails ud,
            @RequestBody InstallmentRequest req
    ) {
        return service.calculateAndSave(getUserId(ud), req);
    }

    @GetMapping("/history")
    public List<InstallmentResponse> history(@AuthenticationPrincipal UserDetails ud) {
        return service.getHistory(getUserId(ud));
    }

    @DeleteMapping("/{id}")
    public void delete(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        service.delete(getUserId(ud), id);
    }
}
