package com.lms.auth.config;

import com.lms.auth.service.JwtService;
import com.lms.auth.service.TokenVersionCacheService;
import com.lms.auth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenVersionCacheService tokenVersionCacheService;
    private final UserService userService; // Redis miss fallback

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // ✅ already authenticated
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // 1) validate signature + expiry
        if (!jwtService.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 2) extract claims
        Long userId = jwtService.extractUserId(token);
        Long tokenVer = jwtService.extractTokenVersion(token);
        var permNames = jwtService.extractPermissions(token);
        String username = jwtService.extractUsername(token);

        if (userId == null || tokenVer == null || username == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // 3) version check via Redis
        Long currentVer = tokenVersionCacheService.get(userId);

        // Redis miss -> DB fallback -> set Redis
        if (currentVer == null) {
            long dbVer = userService.getTokenVersion(userId);
            tokenVersionCacheService.set(userId, dbVer);
            currentVer = dbVer;
        }

        // mismatch => token is stale (permissions changed / user disabled etc.)
        if (!tokenVer.equals(currentVer)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // 4) build authorities from JWT perms
        var authorities = permNames.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        // ✅ principal should be username (or a custom object). userId can be stored in details if needed.
        var authToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}