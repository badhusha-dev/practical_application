package com.example.llm.service;

import com.example.llm.dto.JwtResponse;
import com.example.llm.dto.LoginRequest;
import com.example.llm.dto.RegisterRequest;
import com.example.llm.dto.UserDTO;
import com.example.llm.entity.User;
import com.example.llm.repository.UserRepository;
import com.example.llm.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public JwtResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        
        // Create new user
        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword())
        );
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getUsername());
        
        return new JwtResponse(token, "Bearer", convertToDTO(savedUser));
    }
    
    public JwtResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());
        
        // Find user
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid username or password");
        }
        
        User user = userOpt.get();
        
        // Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid username or password");
        }
        
        log.info("User logged in successfully: {}", user.getUsername());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUsername());
        
        return new JwtResponse(token, "Bearer", convertToDTO(user));
    }
    
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return convertToDTO(user);
    }
    
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.warn("Token validation failed", e);
            return false;
        }
    }
    
    public String getUsernameFromToken(String token) {
        return jwtUtil.getUsernameFromToken(token);
    }
    
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRoles(),
                user.getCreatedAt()
        );
    }
}
