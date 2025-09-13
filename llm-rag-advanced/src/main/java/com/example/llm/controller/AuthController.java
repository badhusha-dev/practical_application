package com.example.llm.controller;

import com.example.llm.dto.JwtResponse;
import com.example.llm.dto.LoginRequest;
import com.example.llm.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    
    private final com.example.llm.service.AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request for username: {}", request.getUsername());
        
        try {
            JwtResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Registration failed for username: {}", request.getUsername(), e);
            return ResponseEntity.status(400).build();
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for username: {}", request.getUsername());
        
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for username: {}", request.getUsername(), e);
            return ResponseEntity.status(401).build();
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            
            if (!authService.validateToken(token)) {
                return ResponseEntity.status(401).build();
            }
            
            String username = authService.getUsernameFromToken(token);
            return ResponseEntity.ok(authService.getUserByUsername(username));
            
        } catch (Exception e) {
            log.error("Error getting current user", e);
            return ResponseEntity.status(401).build();
        }
    }
}
