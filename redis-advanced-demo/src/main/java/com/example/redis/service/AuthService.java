package com.example.redis.service;

import com.example.redis.entity.User;
import com.example.redis.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    public Optional<User> authenticateUser(String username, String password) {
        logger.info("Authenticating user: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            logger.info("User {} authenticated successfully", username);
            return user;
        }
        
        logger.warn("Authentication failed for user: {}", username);
        return Optional.empty();
    }
    
    public User createUser(String username, String password) {
        logger.info("Creating new user: {}", username);
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        
        User user = new User(username, password);
        return userRepository.save(user);
    }
    
    public Optional<User> getUserByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }
}
