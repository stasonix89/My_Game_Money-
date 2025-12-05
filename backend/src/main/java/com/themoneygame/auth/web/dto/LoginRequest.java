package com.themoneygame.auth.web.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {

    private String username;
    private String password;
    private boolean rememberMe;

    public LoginRequest() {}

}

