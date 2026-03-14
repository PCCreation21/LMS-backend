package com.lms.auth.service;

import com.lms.auth.dto.*;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void changePassword(String username, ChangePasswordRequest request);

    AuthResponse refresh(RefreshRequest request);
    void logout(RefreshRequest request);
}
