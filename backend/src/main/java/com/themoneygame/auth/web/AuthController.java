// src/main/java/com/themoneygame/auth/web/AuthController.java
package com.themoneygame.auth.web;

import com.themoneygame.auth.application.UserDetailsImpl;
import com.themoneygame.auth.domain.User;
import com.themoneygame.auth.infrastructure.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    // DTOшки такие же, как на фронте
    public record LoginRequest(String username, String password, boolean rememberMe) {}
    public record AuthUser(Long id, String username, String displayName) {}
    public record LoginResponse(AuthUser user) {}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        // 1) аутентифицируем
        UsernamePasswordAuthenticationToken authReq =
                new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication auth = authenticationManager.authenticate(authReq);

        // 2) кладём в SecurityContext
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);

        // 3) создаём HTTP-сессию -> Spring вернёт Set-Cookie: JSESSIONID=...
        httpRequest.getSession(true);

        // 4) собираем ответ
        UserDetailsImpl principal = (UserDetailsImpl) auth.getPrincipal();
        User user = userRepository.findById(principal.getId())
                .orElseThrow(); // можно сделать красивее

        AuthUser authUser = new AuthUser(
                user.getId(),
                user.getUsername(),
                user.getDisplayName()
        );

        return ResponseEntity.ok(new LoginResponse(authUser));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUser> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl principal)) {
            return ResponseEntity.status(401).build();
        }

        User user = userRepository.findById(principal.getId()).orElseThrow();

        AuthUser authUser = new AuthUser(
                user.getId(),
                user.getUsername(),
                user.getDisplayName()
        );

        return ResponseEntity.ok(authUser);
    }
}
