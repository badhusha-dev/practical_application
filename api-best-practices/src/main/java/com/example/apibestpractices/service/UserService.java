package com.example.apibestpractices.service;

import com.example.apibestpractices.dto.CreateUserRequest;
import com.example.apibestpractices.dto.EntityMapper;
import com.example.apibestpractices.dto.UserDto;
import com.example.apibestpractices.model.User;
import com.example.apibestpractices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final EntityMapper entityMapper;
    
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users with pagination: {}", pageable);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(entityMapper::toUserDto);
    }
    
    @Transactional(readOnly = true)
    public Page<UserDto> getUsersWithFilters(User.Role role, Boolean isActive, String search, Pageable pageable) {
        log.debug("Fetching users with filters - role: {}, isActive: {}, search: {}", role, isActive, search);
        Page<User> users = userRepository.findByFilters(role, isActive, search, pageable);
        return users.map(entityMapper::toUserDto);
    }
    
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        log.debug("Fetching user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        return entityMapper.toUserDto(user);
    }
    
    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return entityMapper.toUserDto(user);
    }
    
    public UserDto createUser(CreateUserRequest request) {
        log.info("Creating new user: {}", request.getUsername());
        
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        User user = entityMapper.toUser(request);
        User savedUser = userRepository.save(user);
        
        log.info("User created successfully with ID: {}", savedUser.getId());
        return entityMapper.toUserDto(savedUser);
    }
    
    public UserDto updateUser(Long id, CreateUserRequest request) {
        log.info("Updating user with ID: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        // Check if username is being changed and already exists
        if (!existingUser.getUsername().equals(request.getUsername()) && 
            userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        
        // Check if email is being changed and already exists
        if (!existingUser.getEmail().equals(request.getEmail()) && 
            userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        // Update user fields
        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setRole(request.getRole());
        
        User updatedUser = userRepository.save(existingUser);
        
        log.info("User updated successfully with ID: {}", updatedUser.getId());
        return entityMapper.toUserDto(updatedUser);
    }
    
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }
        
        userRepository.deleteById(id);
        log.info("User deleted successfully with ID: {}", id);
    }
    
    public void deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
        
        user.setIsActive(false);
        userRepository.save(user);
        
        log.info("User deactivated successfully with ID: {}", id);
    }
    
    @Transactional(readOnly = true)
    public long getUserCountByRole(User.Role role) {
        return userRepository.countByRole(role);
    }
    
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.debug("Fetching all users without pagination");
        List<User> users = userRepository.findAll();
        return entityMapper.toUserDtoList(users);
    }
    
    public boolean isOwner(Long userId, String username) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null && user.getUsername().equals(username);
    }
}
