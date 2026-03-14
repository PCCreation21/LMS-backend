package com.lms.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenVersionCacheService {

    private final StringRedisTemplate redis;

    private static String key(Long userId) {
        return "auth::tokenVersion::" + userId;
    }

    public Long get(Long userId) {
        String v = redis.opsForValue().get(key(userId));
        return v == null ? null : Long.valueOf(v);
    }

    public void set(Long userId, long version) {
        // TTL optional. If you always update on login + permission update, TTL can be long.
        redis.opsForValue().set(key(userId), String.valueOf(version), Duration.ofHours(24));
    }

    public void evict(Long userId) {
        redis.delete(key(userId));
    }
}