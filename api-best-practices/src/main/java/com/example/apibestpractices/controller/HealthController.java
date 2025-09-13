package com.example.apibestpractices.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
@Slf4j
@Tag(name = "Health Check", description = "Health check endpoints")
public class HealthController {

    @GetMapping
    @Operation(
            summary = "Health check",
            description = "Simple health check endpoint to verify the API is running"
    )
    public ResponseEntity<Map<String, Object>> health() {
        log.info("Health check requested");
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "API Best Practices",
                "timestamp", System.currentTimeMillis()
        ));
    }
}
