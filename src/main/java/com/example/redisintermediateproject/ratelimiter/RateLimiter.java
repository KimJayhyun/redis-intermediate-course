package com.example.redisintermediateproject.ratelimiter;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RateLimiter {
    private static final long RATE_LIMIT_TIME_MS = 10_000; // 최대 요청 수
    private static final int MAX_REQUESTS = 5;

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimiter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean allow(String userId) {
        long now = System.currentTimeMillis();

        String redisKey = "rate_limit:" + userId;
        String requestId = UUID.randomUUID().toString();

        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, now - RATE_LIMIT_TIME_MS);

        redisTemplate.opsForZSet().add(redisKey, requestId, now);

        Long count = redisTemplate.opsForZSet().size(redisKey);

        redisTemplate.expire(redisKey, RATE_LIMIT_TIME_MS, TimeUnit.MILLISECONDS);

        return count <= MAX_REQUESTS;
    }

}
