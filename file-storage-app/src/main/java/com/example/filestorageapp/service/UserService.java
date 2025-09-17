package com.example.filestorageapp.service;

import com.example.filestorageapp.entity.User;
import com.example.filestorageapp.entity.UserRole;
import com.example.filestorageapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .enabled(true)
                .storageQuota(1073741824L) // 1GB default
                .usedStorage(0L)
                .build();

        user = userRepository.save(user);
        log.info("User registered successfully: {}", username);
        return user;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> searchUsers(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllUsers(pageable);
        }
        return userRepository.findByUsernameOrEmailContaining(searchTerm.trim(), pageable);
    }

    @Transactional
    public User updateUserStorageQuota(Long userId, Long newQuota) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStorageQuota(newQuota);
        user = userRepository.save(user);
        log.info("Updated storage quota for user {}: {} bytes", user.getUsername(), newQuota);
        return user;
    }

    @Transactional
    public User toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(!user.getEnabled());
        user = userRepository.save(user);
        log.info("Toggled user status for {}: {}", user.getUsername(), user.getEnabled());
        return user;
    }

    @Transactional
    public User changeUserRole(Long userId, UserRole newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(newRole);
        user = userRepository.save(user);
        log.info("Changed role for user {}: {}", user.getUsername(), newRole);
        return user;
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalStorageUsed() {
        return userRepository.findAll().stream()
                .mapToLong(User::getUsedStorage)
                .sum();
    }

    public long getTotalStorageQuota() {
        return userRepository.findAll().stream()
                .mapToLong(User::getStorageQuota)
                .sum();
    }
}
