package com.example.apibestpractices.aspect;

import com.example.apibestpractices.exception.RateLimitExceededException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Aspect
@Component
@Slf4j
@ConditionalOnProperty(name = "app.rate-limit.enabled", havingValue = "true", matchIfMissing = true)
public class RateLimitAspect {
    
    @Autowired(required = false)
    private RedisTemplate<String, String> redisTemplate;
    
    @Value("${app.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;
    
    @Value("${app.rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;
    
    // In-memory fallback for when Redis is not available
    private final ConcurrentHashMap<String, AtomicInteger> inMemoryCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> lastResetTime = new ConcurrentHashMap<>();
    
    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object handleRateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        
        if (!rateLimitEnabled) {
            return joinPoint.proceed();
        }
        
        String clientId = getClientIdentifier();
        log.debug("Rate limiting check for client: {}", clientId);
        
        try {
            if (redisTemplate != null) {
                return handleRateLimitWithRedis(joinPoint, clientId);
            } else {
                log.warn("Redis not available, using in-memory rate limiting for client: {}", clientId);
                return handleRateLimitInMemory(joinPoint, clientId);
            }
        } catch (Exception e) {
            log.error("Rate limiting error for client: {}, falling back to in-memory", clientId, e);
            return handleRateLimitInMemory(joinPoint, clientId);
        }
    }
    
    private Object handleRateLimitWithRedis(ProceedingJoinPoint joinPoint, String clientId) throws Throwable {
        String key = "rate_limit:" + clientId;
        String currentCount = redisTemplate.opsForValue().get(key);
        
        if (currentCount == null) {
            // First request from this client
            redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(1));
            log.debug("Request allowed for client: {}, count: 1", clientId);
            return joinPoint.proceed();
        } else {
            int count = Integer.parseInt(currentCount);
            if (count < requestsPerMinute) {
                redisTemplate.opsForValue().increment(key);
                log.debug("Request allowed for client: {}, count: {}", clientId, count + 1);
                return joinPoint.proceed();
            } else {
                log.warn("Rate limit exceeded for client: {}, count: {}", clientId, count);
                throw new RateLimitExceededException(
                        String.format("Rate limit exceeded. Maximum %d requests per minute allowed", requestsPerMinute));
            }
        }
    }
    
    private Object handleRateLimitInMemory(ProceedingJoinPoint joinPoint, String clientId) throws Throwable {
        long currentTime = System.currentTimeMillis();
        long windowStart = currentTime - (60 * 1000); // 1 minute window
        
        // Clean up old entries
        lastResetTime.entrySet().removeIf(entry -> entry.getValue() < windowStart);
        inMemoryCounters.entrySet().removeIf(entry -> !lastResetTime.containsKey(entry.getKey()));
        
        // Reset counter if window has passed
        if (lastResetTime.getOrDefault(clientId, 0L) < windowStart) {
            inMemoryCounters.put(clientId, new AtomicInteger(0));
            lastResetTime.put(clientId, currentTime);
        }
        
        AtomicInteger counter = inMemoryCounters.computeIfAbsent(clientId, k -> new AtomicInteger(0));
        int currentCount = counter.incrementAndGet();
        
        if (currentCount <= requestsPerMinute) {
            log.debug("Request allowed for client: {}, count: {}", clientId, currentCount);
            return joinPoint.proceed();
        } else {
            log.warn("Rate limit exceeded for client: {}, count: {}", clientId, currentCount);
            throw new RateLimitExceededException(
                    String.format("Rate limit exceeded. Maximum %d requests per minute allowed", requestsPerMinute));
        }
    }
    
    private String getClientIdentifier() {
        // Try to get authenticated user first
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            return authentication.getName();
        }
        
        // Fall back to IP address
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getHeader("X-Real-IP");
            }
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            }
            return clientIp;
        }
        
        return "unknown";
    }
    
    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}