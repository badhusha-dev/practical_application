package com.example.thymeleafmongoflywayapp.service;

import com.example.thymeleafmongoflywayapp.entity.User;
import com.example.thymeleafmongoflywayapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    
    public List<User> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll();
    }
    
    public Page<User> getAllUsersWithPagination(int page, int size, String sortBy, String sortDir) {
        log.debug("Fetching users with pagination - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                 page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findAllWithPagination(pageable);
    }
    
    public Page<User> searchUsers(String searchTerm, int page, int size, String sortBy, String sortDir) {
        log.debug("Searching users with term: {}", searchTerm);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        return userRepository.findByNameOrEmailContainingIgnoreCase(searchTerm, pageable);
    }
    
    public Optional<User> getUserById(String id) {
        log.debug("Fetching user by ID: {}", id);
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);
    }
    
    public User saveUser(User user) {
        log.info("Saving user: {}", user.getEmail());
        
        if (user.getId() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User updateUser(String id, User userDetails) {
        log.info("Updating user with ID: {}", id);
        
        return userRepository.findById(id)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    user.setAge(userDetails.getAge());
                    user.setUpdatedAt(LocalDateTime.now());
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    
    public void deleteUser(String id) {
        log.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        
        userRepository.deleteById(id);
    }
    
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public boolean emailExistsForOtherUser(String email, String userId) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && !user.get().getId().equals(userId);
    }
    
    public long getUserCount() {
        return userRepository.count();
    }
    
    public List<User> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        log.debug("Fetching users by age range: {} - {}", minAge, maxAge);
        return userRepository.findByAgeBetween(minAge, maxAge);
    }
}
