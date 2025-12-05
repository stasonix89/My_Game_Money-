package com.themoneygame.auth.web.dto;

import lombok.Getter;

import java.util.Set;
@Getter
public class AuthResponse {

    private Long id;
    private String username;
    private String displayName;
    private Set<String> roles;

    public AuthResponse(Long id, String username, String displayName, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.roles = roles;
    }
}
