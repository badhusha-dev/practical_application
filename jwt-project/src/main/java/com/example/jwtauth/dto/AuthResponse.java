package com.example.jwtauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public class AuthResponse {
    
    @Schema(description = "User registration response")
    public static class RegisterResponse {
        @Schema(description = "Success message", example = "User registered successfully")
        private String message;
        
        @Schema(description = "Registered username", example = "john_doe")
        private String username;
        
        @Schema(description = "Assigned roles", example = "[\"USER\"]")
        private List<String> roles;
        
        public RegisterResponse(String message, String username, List<String> roles) {
            this.message = message;
            this.username = username;
            this.roles = roles;
        }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }
    
    @Schema(description = "User login response")
    public static class LoginResponse {
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String token;
        
        @Schema(description = "Authenticated username", example = "john_doe")
        private String username;
        
        @Schema(description = "User authorities", example = "[\"ROLE_USER\"]")
        private List<String> authorities;
        
        public LoginResponse(String token, String username, List<String> authorities) {
            this.token = token;
            this.username = username;
            this.authorities = authorities;
        }
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public List<String> getAuthorities() { return authorities; }
        public void setAuthorities(List<String> authorities) { this.authorities = authorities; }
    }
    
    @Schema(description = "User information response")
    public static class UserResponse {
        @Schema(description = "User ID", example = "1")
        private Integer id;
        
        @Schema(description = "Username", example = "john_doe")
        private String username;
        
        @Schema(description = "User roles", example = "[\"USER\"]")
        private List<String> roles;
        
        public UserResponse(Integer id, String username, List<String> roles) {
            this.id = id;
            this.username = username;
            this.roles = roles;
        }
        
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }
    }
    
    @Schema(description = "Error response")
    public static class ErrorResponse {
        @Schema(description = "Error message", example = "Invalid username or password")
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
