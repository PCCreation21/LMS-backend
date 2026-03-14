package com.lms.gateway.Service;



        import lombok.RequiredArgsConstructor;
        import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
        import org.springframework.stereotype.Service;
        import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenVersionRedisService {

    private final ReactiveStringRedisTemplate redis;

    private static String key(Long userId) {
        return "auth::tokenVersion::" + userId;
    }

    public Mono<Long> getCurrentVersion(Long userId) {
        return redis.opsForValue()
                .get(key(userId))
                .map(Long::valueOf);
    }
}