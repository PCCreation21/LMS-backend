package com.lms.gateway.filter;

import com.lms.gateway.Service.TokenVersionRedisService;
import com.lms.gateway.Service.TokenVersionRedisService;
import com.lms.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator validator;
    private final JwtUtil jwtUtil;
    private final TokenVersionRedisService tokenVersionRedisService;

    public AuthenticationFilter(RouteValidator validator,
                                JwtUtil jwtUtil,
                                TokenVersionRedisService tokenVersionRedisService) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
        this.tokenVersionRedisService = tokenVersionRedisService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            if (!validator.isSecured.test(request)) {
                return chain.filter(exchange);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);

            Claims claims;
            try {
                jwtUtil.validateToken(token);
                claims = jwtUtil.extractAllClaims(token);
            } catch (ExpiredJwtException e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED, "JWT token has expired");
            } catch (Exception e) {
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid JWT token");
            }

            Long uid = getLongClaim(claims, "uid");
            Long tokenVer = getLongClaim(claims, "ver");

            if (uid == null || tokenVer == null) {
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Required token claims are missing");
            }

            String permissionsHeader = buildPermissionsHeader(claims);

            return tokenVersionRedisService.getCurrentVersion(uid)
                    .switchIfEmpty(Mono.error(new RuntimeException("REDIS_MISS")))
                    .flatMap(currentVer -> {
                        if (!tokenVer.equals(currentVer)) {
                            return onError(exchange, HttpStatus.FORBIDDEN, "Token is no longer valid");
                        }

                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-User-Id", String.valueOf(uid))
                                .header("X-User-Name", claims.getSubject())
                                .header("X-User-Permissions", permissionsHeader)
                                .build();

                        return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    })
                    .onErrorResume(ex ->
                            onError(exchange, HttpStatus.UNAUTHORIZED, "Unable to validate token version")
                    );
        };
    }

    private String buildPermissionsHeader(Claims claims) {
        Object permsObj = claims.get("perms");

        if (permsObj instanceof List<?> permsList) {
            return permsList.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        }

        return "";
    }

    private Long getLongClaim(Claims claims, String key) {
        Object v = claims.get(key);
        if (v == null) return null;
        if (v instanceof Integer i) return i.longValue();
        if (v instanceof Long l) return l;
        try {
            return Long.valueOf(v.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {
                  "success": false,
                  "message": "%s"
                }
                """.formatted(message);

        var buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
    }
}