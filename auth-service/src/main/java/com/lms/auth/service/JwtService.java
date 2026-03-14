package com.lms.auth.service;

import com.lms.auth.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // ====== TOKEN GENERATION ======
    public String generateToken(User user) {

        List<String> permissions = user.getPermissions() == null
                ? List.of()
                : user.getPermissions().stream().map(Enum::name).collect(Collectors.toList());

        return Jwts.builder()
                .claim("perms", permissions)                 // ✅ permissions list
                .claim("uid", user.getId())                  // ✅ userId
                .claim("ver", user.getTokenVersion())        // ✅ tokenVersion
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ====== VALIDATION ======
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ====== EXTRACTION HELPERS ======
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object v = extractAllClaims(token).get("uid");
        if (v == null) return null;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof Long l) return l;
        return Long.valueOf(v.toString());
    }

    public Long extractTokenVersion(String token) {
        Object v = extractAllClaims(token).get("ver");
        if (v == null) return null;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof Long l) return l;
        return Long.valueOf(v.toString());
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        Object v = extractAllClaims(token).get("perms");
        if (v == null) return List.of();
        return (List<String>) v; // stored as list
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}