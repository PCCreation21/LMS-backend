package com.lms.auth.service;

import com.lms.auth.dto.AuthResponse;
import com.lms.auth.dto.ChangePasswordRequest;
import com.lms.auth.dto.LoginRequest;
import com.lms.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void changePassword(String username, ChangePasswordRequest request);
}
