package com.example.jwtauth.controller;

import com.example.jwtauth.dto.AuthRequest;
import com.example.jwtauth.dto.AuthResponse;
import com.example.jwtauth.entity.User;
import com.example.jwtauth.security.JwtUtil;
import com.example.jwtauth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authentication", description = "JWT Authentication API endpoints")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private com.example.jwtauth.security.JwtUserDetailsService userDetailsService;
    
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided username and password. The user will be assigned default roles."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "User registered successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.RegisterResponse.class),
                examples = @ExampleObject(
                    value = "{\"message\": \"User registered successfully\", \"username\": \"john_doe\", \"roles\": [\"USER\"]}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Bad request - validation errors or user already exists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.ErrorResponse.class),
                examples = @ExampleObject(
                    value = "{\"error\": \"Username already exists\"}"
                )
            )
        )
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody AuthRequest.RegisterRequest request) {
        try {
            User user = userService.registerUser(request.getUsername(), request.getPassword());
            
            AuthResponse.RegisterResponse response = new AuthResponse.RegisterResponse(
                "User registered successfully",
                user.getUsername(),
                user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList())
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse.ErrorResponse(e.getMessage()));
        }
    }
    
    @Operation(
        summary = "Authenticate user and get JWT tokens",
        description = "Authenticates a user with username and password, returns access and refresh tokens."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Authentication successful",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"refreshToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"username\": \"john_doe\", \"authorities\": [\"ROLE_USER\"]}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.ErrorResponse.class),
                examples = @ExampleObject(
                    value = "{\"error\": \"Invalid username or password\"}"
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthRequest.LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);
            
            Map<String, Object> fullResponse = new HashMap<>();
            fullResponse.put("accessToken", accessToken);
            fullResponse.put("refreshToken", refreshToken);
            fullResponse.put("username", userDetails.getUsername());
            fullResponse.put("authorities", userDetails.getAuthorities().stream()
                    .map(auth -> auth.getAuthority())
                    .collect(Collectors.toList()));
            
            return ResponseEntity.ok(fullResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse.ErrorResponse("Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse.ErrorResponse("Authentication failed"));
        }
    }
    
    @Operation(
        summary = "Refresh access token",
        description = "Uses a valid refresh token to generate a new access token."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Token refreshed successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\", \"username\": \"john_doe\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Invalid or expired refresh token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.ErrorResponse.class),
                examples = @ExampleObject(
                    value = "{\"error\": \"Invalid refresh token\"}"
                )
            )
        )
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null) {
                return ResponseEntity.badRequest().body(new AuthResponse.ErrorResponse("Refresh token is required"));
            }
            
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtUtil.validateRefreshToken(refreshToken, userDetails)) {
                String newAccessToken = jwtUtil.generateAccessToken(userDetails);
                
                Map<String, Object> response = new HashMap<>();
                response.put("accessToken", newAccessToken);
                response.put("username", userDetails.getUsername());
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse.ErrorResponse("Invalid refresh token"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse.ErrorResponse("Token refresh failed"));
        }
    }
    
    @Operation(
        summary = "Logout user",
        description = "Logs out the current user. In a production environment, this would invalidate the token."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Logout successful",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = "{\"error\": \"Logout successful\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Bad request - missing authorization header",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.ErrorResponse.class)
            )
        )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(new AuthResponse.ErrorResponse("Authorization header is required"));
            }
            
            // In a real application, you might want to blacklist the token
            // For now, we'll just return a success message
            return ResponseEntity.ok(new AuthResponse.ErrorResponse("Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse.ErrorResponse("Logout failed"));
        }
    }
    
    @Operation(
        summary = "Get current user information",
        description = "Retrieves the current authenticated user's information including roles."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "User information retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.UserResponse.class),
                examples = @ExampleObject(
                    value = "{\"id\": 1, \"username\": \"john_doe\", \"roles\": [\"USER\"]}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - invalid or expired token",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.ErrorResponse.class),
                examples = @ExampleObject(
                    value = "{\"error\": \"Invalid or expired token\"}"
                )
            )
        )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse.ErrorResponse("Authorization header is required"));
            }
            
            String token = authHeader.substring(7); // Remove "Bearer "
            String username = jwtUtil.extractUsername(token);
            User user = userService.findByUsername(username);
            
            AuthResponse.UserResponse response = new AuthResponse.UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toList())
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse.ErrorResponse("Invalid or expired token"));
        }
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
