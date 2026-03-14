package com.lms.auth.service;

import com.lms.auth.entity.RefreshToken;
import com.lms.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // first login expiry window
    private static final int REFRESH_DAYS = 7;

    /**
     * First login / register
     * Creates refresh token with absolute expiry
     */
    public String createRefreshToken(Long userId) {
        LocalDateTime absoluteExpiry = LocalDateTime.now().plusDays(REFRESH_DAYS);
        return createRefreshToken(userId, absoluteExpiry);
    }

    /**
     * Rotation (used during refresh)
     * Keeps SAME expiry date
     */
    public String createRefreshToken(Long userId, LocalDateTime expiresAt) {

        String raw = UUID.randomUUID().toString() + UUID.randomUUID();
        String hash = sha256(raw);

        RefreshToken rt = RefreshToken.builder()
                .userId(userId)
                .tokenHash(hash)
                .expiresAt(expiresAt) // keep original expiry
                .revoked(false)
                .build();

        refreshTokenRepository.save(rt);
        return raw;
    }

    /**
     * Validate refresh token
     */
    public RefreshToken validate(String rawToken) {
        String hash = sha256(rawToken);

        RefreshToken rt = refreshTokenRepository
                .findByTokenHashAndRevokedFalse(hash)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired");
        }

        return rt;
    }

    /**
     * Revoke refresh token (rotation / logout)
     */
    public void revoke(String rawToken) {
        String hash = sha256(rawToken);

        refreshTokenRepository
                .findByTokenHashAndRevokedFalse(hash)
                .ifPresent(rt -> {
                    rt.setRevoked(true);
                    refreshTokenRepository.save(rt);
                });
    }

    /**
     * Hash refresh token before storing
     */
    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("Hashing error", e);
        }
    }
}