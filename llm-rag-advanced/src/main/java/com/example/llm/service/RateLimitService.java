package com.example.llm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    @Value("${app.rateLimit.requestsPerMinute:60}")
    private int requestsPerMinute;
    
    @Value("${app.rateLimit.windowSizeMinutes:1}")
    private int windowSizeMinutes;
    
    public boolean isAllowed(UUID userId) {
        String key = "rate_limit:" + userId + ":" + getCurrentMinuteBucket();
        
        try {
            // Get current count
            String currentCountStr = redisTemplate.opsForValue().get(key);
            int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
            
            if (currentCount >= requestsPerMinute) {
                log.warn("Rate limit exceeded for user: {}", userId);
                return false;
            }
            
            // Increment counter
            Long newCount = redisTemplate.opsForValue().increment(key);
            
            // Set expiration if this is the first request
            if (newCount == 1) {
                redisTemplate.expire(key, Duration.ofMinutes(windowSizeMinutes));
            }
            
            log.debug("Rate limit check for user {}: {}/{} requests", userId, newCount, requestsPerMinute);
            return true;
            
        } catch (Exception e) {
            log.error("Error checking rate limit for user: {}", userId, e);
            // In case of Redis error, allow the request (fail open)
            return true;
        }
    }
    
    public int getRemainingRequests(UUID userId) {
        String key = "rate_limit:" + userId + ":" + getCurrentMinuteBucket();
        
        try {
            String currentCountStr = redisTemplate.opsForValue().get(key);
            int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
            return Math.max(0, requestsPerMinute - currentCount);
        } catch (Exception e) {
            log.error("Error getting remaining requests for user: {}", userId, e);
            return requestsPerMinute;
        }
    }
    
    public void resetRateLimit(UUID userId) {
        String keyPattern = "rate_limit:" + userId + ":*";
        redisTemplate.delete(redisTemplate.keys(keyPattern));
        log.info("Rate limit reset for user: {}", userId);
    }
    
    private long getCurrentMinuteBucket() {
        return System.currentTimeMillis() / (windowSizeMinutes * 60 * 1000);
    }
}
