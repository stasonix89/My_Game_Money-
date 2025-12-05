// src/main/java/com/themoneygame/core/config/SecurityConfig.java
package com.themoneygame.core.config;

import com.themoneygame.auth.application.UserDetailsServiceImpl;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String REMEMBER_ME_KEY = "the-money-game-remember-me-key";

    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // JSESSIONID: SameSite=None (чтобы не блочился)
    @Bean
    public CookieSameSiteSupplier jsessionIdSameSiteSupplier() {
        return CookieSameSiteSupplier.ofNone().whenHasName("JSESSIONID");
    }

    // ---- CORS ----
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // фронт
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));
        // или так, чтобы не упираться в порт, если что:
        // config.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));

        config.setAllowCredentials(true);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // ---- password encoder ----
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ---- authentication manager ----
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // ---- remember-me ----
    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices services =
                new TokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetailsService);
        services.setParameter("rememberMe");
        services.setCookieName("remember-me");
        services.setUseSecureCookie(false); // локально без https
        services.setTokenValiditySeconds(60 * 60 * 24 * 30);
        return services;
    }

    // ---- основная security-конфигурация ----
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            RememberMeServices rememberMeServices
    ) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                // ВАЖНО: сохранять SecurityContext автоматически в сессию
                .securityContext(ctx -> ctx.requireExplicitSave(false))

                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )

                .userDetailsService(userDetailsService)

                .rememberMe(rm -> rm
                        .rememberMeServices(rememberMeServices)
                        .key(REMEMBER_ME_KEY)
                )

                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .deleteCookies("JSESSIONID", "remember-me")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                );

        return http.build();
    }
}
