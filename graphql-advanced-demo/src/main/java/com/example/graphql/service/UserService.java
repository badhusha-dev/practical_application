package com.example.graphql.service;

import com.example.graphql.dto.UserDTO;
import com.example.graphql.entity.User;
import com.example.graphql.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Cacheable(value = "users", key = "'all'")
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users from database");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toUserDTO)
                .toList();
    }
    
    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        log.info("Fetching user with id: {} from database", id);
        return userRepository.findById(id)
                .map(this::toUserDTO);
    }
    
    @Cacheable(value = "users", key = "#username")
    @Transactional(readOnly = true)
    public Optional<User> getUserByUsername(String username) {
        log.info("Fetching user by username: {} from database", username);
        return userRepository.findByUsername(username);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO createUser(String username, String email, String password) {
        log.info("Creating new user: {}", username);
        
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        
        User user = new User(username, email, passwordEncoder.encode(password));
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with id: {}", savedUser.getId());
        return toUserDTO(savedUser);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public UserDTO updateUser(Long id, String username, String email) {
        log.info("Updating user with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists: " + username);
        }
        
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists: " + email);
        }
        
        user.setUsername(username);
        user.setEmail(email);
        
        User savedUser = userRepository.save(user);
        return toUserDTO(savedUser);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public void deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    public boolean authenticateUser(String username, String password) {
        log.info("Authenticating user: {}", username);
        Optional<User> user = userRepository.findByUsername(username);
        
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            log.info("User {} authenticated successfully", username);
            return true;
        }
        
        log.warn("Authentication failed for user: {}", username);
        return false;
    }
    
    private UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
