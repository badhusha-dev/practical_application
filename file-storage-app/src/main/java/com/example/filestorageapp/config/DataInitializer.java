package com.example.filestorageapp.config;

import com.example.filestorageapp.entity.User;
import com.example.filestorageapp.entity.UserRole;
import com.example.filestorageapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@example.com")
                    .role(UserRole.ROLE_ADMIN)
                    .enabled(true)
                    .storageLimitBytes(10L * 1024 * 1024 * 1024) // 10GB
                    .currentStorageBytes(0L)
                    .build();
            
            userRepository.save(admin);
            log.info("Created admin user: admin/admin123");
        }

        // Create regular user if not exists
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .email("user@example.com")
                    .role(UserRole.ROLE_USER)
                    .enabled(true)
                    .storageLimitBytes(1L * 1024 * 1024 * 1024) // 1GB
                    .currentStorageBytes(0L)
                    .build();
            
            userRepository.save(user);
            log.info("Created user: user/user123");
        }

        log.info("Data initialization completed!");
    }
}
