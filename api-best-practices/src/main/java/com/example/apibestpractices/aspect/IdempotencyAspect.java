package com.example.apibestpractices.aspect;

import com.example.apibestpractices.exception.IdempotencyKeyException;
import com.example.apibestpractices.model.IdempotencyKey;
import com.example.apibestpractices.repository.IdempotencyKeyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyAspect {
    
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;
    
    @Value("${app.idempotency.enabled:true}")
    private boolean idempotencyEnabled;
    
    @Value("${app.idempotency.key-header:Idempotency-Key}")
    private String idempotencyKeyHeader;
    
    @Value("${app.idempotency.ttl-hours:24}")
    private int ttlHours;
    
    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public Object handleIdempotency(ProceedingJoinPoint joinPoint) throws Throwable {
        
        if (!idempotencyEnabled) {
            return joinPoint.proceed();
        }
        
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            return joinPoint.proceed();
        }
        
        String idempotencyKey = request.getHeader(idempotencyKeyHeader);
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            log.debug("No idempotency key provided, proceeding with request");
            return joinPoint.proceed();
        }
        
        log.debug("Processing request with idempotency key: {}", idempotencyKey);
        
        // Generate request hash for validation
        String requestHash = generateRequestHash(request, joinPoint.getArgs());
        
        // Check if idempotency key already exists
        Optional<IdempotencyKey> existingKey = idempotencyKeyRepository
                .findByKeyValueAndExpiresAtAfter(idempotencyKey, OffsetDateTime.now());
        
        if (existingKey.isPresent()) {
            IdempotencyKey key = existingKey.get();
            
            // Validate request hash matches
            if (!key.getRequestHash().equals(requestHash)) {
                throw new IdempotencyKeyException(
                        "Idempotency key already exists with different request content");
            }
            
            log.debug("Returning cached response for idempotency key: {}", idempotencyKey);
            
            // Return cached response
            return ResponseEntity.status(key.getHttpStatus())
                    .body(objectMapper.readValue(key.getResponseBody(), Object.class));
        }
        
        // Execute the original method
        Object result = joinPoint.proceed();
        
        // Store the result for future idempotent requests
        if (result instanceof ResponseEntity<?> responseEntity) {
            String responseBody = objectMapper.writeValueAsString(responseEntity.getBody());
            Integer httpStatus = responseEntity.getStatusCode().value();
            
            OffsetDateTime expiresAt = OffsetDateTime.now().plusHours(ttlHours);
            
            IdempotencyKey newKey = new IdempotencyKey(
                    idempotencyKey,
                    requestHash,
                    responseBody,
                    httpStatus,
                    expiresAt
            );
            
            idempotencyKeyRepository.save(newKey);
            log.debug("Stored idempotency key: {} with response", idempotencyKey);
        }
        
        return result;
    }
    
    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
    
    private String generateRequestHash(HttpServletRequest request, Object[] args) {
        try {
            StringBuilder hashInput = new StringBuilder();
            hashInput.append(request.getMethod());
            hashInput.append(request.getRequestURI());
            
            // Include request body hash if present
            if (args.length > 0) {
                String requestBody = objectMapper.writeValueAsString(args[0]);
                hashInput.append(requestBody);
            }
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(hashInput.toString().getBytes(StandardCharsets.UTF_8));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            log.error("Error generating request hash", e);
            return "unknown";
        }
    }
}
