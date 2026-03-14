package com.lms.auth.service;

import com.lms.auth.dto.*;
import com.lms.auth.entity.RefreshToken;
import com.lms.auth.entity.User;
import com.lms.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenVersionCacheService tokenVersionCacheService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByNic(request.getNic())) {
            throw new RuntimeException("NIC already registered");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .nic(request.getNic())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .permissions(request.getPermissions())
                .createdDate(LocalDate.now())
                .enabled(true)
                .tokenVersion(0L)
                .build();

        userRepository.save(user);

        // keep Redis in sync for gateway version-check
        tokenVersionCacheService.set(user.getId(), user.getTokenVersion());

        String accessToken = jwtService.generateToken(user);

        // first refresh token -> absolute expiry starts here
        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                user.getPermissions(),
                "User registered successfully"
        );
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        // keep Redis in sync for gateway version-check
        tokenVersionCacheService.set(user.getId(), user.getTokenVersion());

        String accessToken = jwtService.generateToken(user);

        // first refresh token -> absolute expiry starts here
        String refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getUsername(),
                user.getPermissions(),
                "Login successful"
        );
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        // validate existing refresh token
        RefreshToken rt = refreshTokenService.validate(request.getRefreshToken());

        User user = userRepository.findById(rt.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account is disabled");
        }

        // ensure Redis has latest version
        tokenVersionCacheService.set(user.getId(), user.getTokenVersion());

        // create new access token
        String accessToken = jwtService.generateToken(user);

        // rotate refresh token but KEEP SAME ABSOLUTE EXPIRY
        refreshTokenService.revoke(request.getRefreshToken());
        String newRefreshToken = refreshTokenService.createRefreshToken(
                user.getId(),
                rt.getExpiresAt()
        );

        return new AuthResponse(
                accessToken,
                newRefreshToken,
                user.getUsername(),
                user.getPermissions(),
                "Token refreshed"
        );
    }

    @Override
    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenService.revoke(request.getRefreshToken());
    }

    @Override
    @Transactional
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("New passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}