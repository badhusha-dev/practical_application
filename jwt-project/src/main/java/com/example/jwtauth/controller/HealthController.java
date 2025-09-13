package com.example.jwtauth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "Health Check", description = "Application health monitoring endpoints")
public class HealthController {
    
    @Operation(
        summary = "Health check endpoint",
        description = "Returns the current status of the JWT Authentication service"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Service is running and healthy",
            content = @io.swagger.v3.oas.annotations.media.Content(
                mediaType = "application/json",
                examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                    value = "{\"status\": \"UP\", \"message\": \"JWT Authentication Service is running\", \"timestamp\": 1703123456789}"
                )
            )
        )
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "JWT Authentication Service is running");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
