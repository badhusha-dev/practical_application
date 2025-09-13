package com.example.apibestpractices.util;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response wrapper")
public class ApiResponse<T> {
    
    @Schema(description = "Indicates if the request was successful", example = "true")
    private boolean success;
    
    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;
    
    @Schema(description = "Response data payload")
    private T data;
    
    @Schema(description = "List of error messages if any")
    private List<String> errors;
    
    @Schema(description = "Response timestamp", example = "2024-01-01T00:00:00Z")
    private OffsetDateTime timestamp;
    
    @Schema(description = "Trace ID for request tracking", example = "abc123-def456")
    private String traceId;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, null, OffsetDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, null, OffsetDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, null, OffsetDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors, OffsetDateTime.now(), null);
    }
    
    public static <T> ApiResponse<T> error(String message, String traceId) {
        return new ApiResponse<>(false, message, null, null, OffsetDateTime.now(), traceId);
    }
}
