package com.lms.auth.dto;

import com.lms.auth.enums.Permission;
import lombok.Data;

import java.util.Set;

@Data
public class AuthResponse {

    private String token;          // access token
    private String refreshToken;   // refresh token
    private String username;
    private Set<Permission> permissions;
    private String message;

    public AuthResponse(String token,
                        String refreshToken,
                        String username,
                        Set<Permission> permissions,
                        String message) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
        this.permissions = permissions;
        this.message = message;
    }
}