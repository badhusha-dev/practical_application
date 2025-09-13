package com.example.apibestpractices.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String traceId;
    private Map<String, String> validationErrors;
}
