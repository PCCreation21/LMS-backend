package com.lms.auth.dto;

import com.lms.auth.enums.Permission;
import lombok.Data;

import java.util.Set;

@Data
public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private Set<Permission> permissions;
    private String message;

    public AuthResponse(String token, String username, String role, Set<Permission> permissions, String message) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.permissions = permissions;
        this.message = message;
    }
}
