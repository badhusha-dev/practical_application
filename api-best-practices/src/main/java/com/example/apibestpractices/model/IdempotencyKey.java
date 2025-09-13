package com.example.apibestpractices.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "idempotency_keys")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyKey {
    
    @Id
    @Column(name = "key_value", length = 255)
    private String keyValue;
    
    @Column(name = "request_hash", length = 64)
    private String requestHash;
    
    @Column(name = "response_body", columnDefinition = "TEXT")
    private String responseBody;
    
    @Column(name = "http_status")
    private Integer httpStatus;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    
    @Column(name = "expires_at", nullable = false)
    private OffsetDateTime expiresAt;
    
    public IdempotencyKey(String keyValue, String requestHash, String responseBody, Integer httpStatus, OffsetDateTime expiresAt) {
        this.keyValue = keyValue;
        this.requestHash = requestHash;
        this.responseBody = responseBody;
        this.httpStatus = httpStatus;
        this.expiresAt = expiresAt;
    }
}
