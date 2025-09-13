package com.example.redis.controller;

import com.example.redis.entity.User;
import com.example.redis.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestParam String username, 
                                                   @RequestParam String password,
                                                   HttpSession session) {
        logger.info("POST /auth/login - Attempting login for user: {}", username);
        
        Optional<User> user = authService.authenticateUser(username, password);
        
        Map<String, Object> response = new HashMap<>();
        
        if (user.isPresent()) {
            // Store user in session (backed by Redis)
            session.setAttribute("user", user.get());
            session.setAttribute("username", user.get().getUsername());
            
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", user.get());
            
            logger.info("User {} logged in successfully, session stored in Redis", username);
        } else {
            response.put("success", false);
            response.put("message", "Invalid username or password");
            
            logger.warn("Login failed for user: {}", username);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        logger.info("GET /auth/me - Getting current user from Redis session");
        
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        
        Map<String, Object> response = new HashMap<>();
        
        if (user != null) {
            response.put("success", true);
            response.put("user", user);
            response.put("sessionId", session.getId());
            
            logger.info("Current user retrieved from Redis session: {}", username);
        } else {
            response.put("success", false);
            response.put("message", "No active session found");
            
            logger.warn("No active session found");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        String username = (String) session.getAttribute("username");
        logger.info("POST /auth/logout - Logging out user: {}", username);
        
        session.invalidate();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logout successful");
        
        logger.info("User {} logged out, session removed from Redis", username);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> register(@RequestParam String username, 
                                                       @RequestParam String password) {
        logger.info("POST /auth/register - Registering new user: {}", username);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user = authService.createUser(username, password);
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", user);
            
            logger.info("User {} registered successfully", username);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            
            logger.warn("Registration failed for user: {} - {}", username, e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}
