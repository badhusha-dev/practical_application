package com.example.apibestpractices.dto;

import com.example.apibestpractices.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User data transfer object")
public class UserDto {
    
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;
    
    @Schema(description = "Username for authentication", example = "john_doe")
    private String username;
    
    @Schema(description = "Email address", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "First name", example = "John")
    private String firstName;
    
    @Schema(description = "Last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "Full name (computed field)", example = "John Doe")
    private String fullName;
    
    @Schema(description = "User role", example = "USER", allowableValues = {"USER", "ADMIN", "MANAGER"})
    private User.Role role;
    
    @Schema(description = "Whether the user account is active", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Account creation timestamp", example = "2024-01-01T00:00:00Z")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Last update timestamp", example = "2024-01-01T00:00:00Z")
    private OffsetDateTime updatedAt;
}
