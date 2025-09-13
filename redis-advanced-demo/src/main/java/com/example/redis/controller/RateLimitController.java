package com.example.redis.controller;

import com.example.redis.service.RateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RateLimitController {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitController.class);
    
    @Autowired
    private RateLimitService rateLimitService;
    
    @GetMapping("/data")
    public ResponseEntity<Map<String, Object>> getData(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String userId = username != null ? username : session.getId();
        
        logger.info("GET /api/data - Rate limit check for user: {}", userId);
        
        Map<String, Object> response = new HashMap<>();
        
        if (rateLimitService.isAllowed(userId)) {
            int remainingRequests = rateLimitService.getRemainingRequests(userId);
            
            response.put("success", true);
            response.put("message", "Data retrieved successfully");
            response.put("data", "This is protected data from the API");
            response.put("remainingRequests", remainingRequests);
            response.put("userId", userId);
            
            logger.info("Rate limit check passed for user: {}, remaining requests: {}", userId, remainingRequests);
            
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Rate limit exceeded. Maximum 5 requests per minute allowed.");
            response.put("userId", userId);
            
            logger.warn("Rate limit exceeded for user: {}", userId);
            
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
        }
    }
    
    @PostMapping("/reset-rate-limit")
    public ResponseEntity<Map<String, Object>> resetRateLimit(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String userId = username != null ? username : session.getId();
        
        logger.info("POST /api/reset-rate-limit - Resetting rate limit for user: {}", userId);
        
        rateLimitService.resetRateLimit(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Rate limit reset successfully");
        response.put("userId", userId);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/rate-limit-status")
    public ResponseEntity<Map<String, Object>> getRateLimitStatus(HttpSession session) {
        String username = (String) session.getAttribute("username");
        String userId = username != null ? username : session.getId();
        
        int remainingRequests = rateLimitService.getRemainingRequests(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("remainingRequests", remainingRequests);
        response.put("maxRequests", 5);
        response.put("windowDuration", "1 minute");
        
        return ResponseEntity.ok(response);
    }
}
