package com.example.apibestpractices.exception;

public class IdempotencyKeyException extends RuntimeException {
    
    public IdempotencyKeyException(String message) {
        super(message);
    }
    
    public IdempotencyKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
