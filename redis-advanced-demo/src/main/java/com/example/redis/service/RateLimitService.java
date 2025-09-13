package com.example.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitService {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitService.class);
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    private static final int MAX_REQUESTS = 5;
    private static final Duration WINDOW_DURATION = Duration.ofMinutes(1);
    
    public boolean isAllowed(String userId) {
        String key = "rate_limit:" + userId;
        
        try {
            // Get current count
            String currentCountStr = redisTemplate.opsForValue().get(key);
            int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
            
            if (currentCount >= MAX_REQUESTS) {
                logger.warn("Rate limit exceeded for user: {}", userId);
                return false;
            }
            
            // Increment counter
            Long newCount = redisTemplate.opsForValue().increment(key);
            
            // Set expiration if this is the first request
            if (newCount == 1) {
                redisTemplate.expire(key, WINDOW_DURATION);
            }
            
            logger.info("Rate limit check for user {}: {}/{} requests", userId, newCount, MAX_REQUESTS);
            return true;
            
        } catch (Exception e) {
            logger.error("Error checking rate limit for user: {}", userId, e);
            // In case of Redis error, allow the request (fail open)
            return true;
        }
    }
    
    public int getRemainingRequests(String userId) {
        String key = "rate_limit:" + userId;
        
        try {
            String currentCountStr = redisTemplate.opsForValue().get(key);
            int currentCount = currentCountStr != null ? Integer.parseInt(currentCountStr) : 0;
            return Math.max(0, MAX_REQUESTS - currentCount);
        } catch (Exception e) {
            logger.error("Error getting remaining requests for user: {}", userId, e);
            return MAX_REQUESTS;
        }
    }
    
    public void resetRateLimit(String userId) {
        String key = "rate_limit:" + userId;
        redisTemplate.delete(key);
        logger.info("Rate limit reset for user: {}", userId);
    }
}
