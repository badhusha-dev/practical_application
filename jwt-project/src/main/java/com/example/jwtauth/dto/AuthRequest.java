package com.example.jwtauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.example.jwtauth.validation.PasswordStrength;

public class AuthRequest {
    
    @Schema(description = "User registration request")
    public static class RegisterRequest {
        @Schema(description = "Username for the new account", example = "john_doe", minLength = 3, maxLength = 50)
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        private String username;
        
        @Schema(description = "Password for the new account", example = "SecurePass123!", minLength = 6, maxLength = 100)
        @NotBlank(message = "Password is required")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        @PasswordStrength
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    @Schema(description = "User login request")
    public static class LoginRequest {
        @Schema(description = "Username for authentication", example = "john_doe")
        @NotBlank(message = "Username is required")
        private String username;
        
        @Schema(description = "Password for authentication", example = "SecurePass123!")
        @NotBlank(message = "Password is required")
        private String password;
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
