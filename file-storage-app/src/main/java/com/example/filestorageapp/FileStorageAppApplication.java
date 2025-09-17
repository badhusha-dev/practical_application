package com.example.filestorageapp;

import com.example.filestorageapp.entity.User;
import com.example.filestorageapp.entity.UserRole;
import com.example.filestorageapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class FileStorageAppApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(FileStorageAppApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if not exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(UserRole.ADMIN)
                    .enabled(true)
                    .storageQuota(10737418240L) // 10GB
                    .build();
            
            userRepository.save(admin);
            log.info("Default admin user created: admin/admin123");
        }

        // Create default test user if not exists
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = User.builder()
                    .username("user")
                    .email("user@example.com")
                    .password(passwordEncoder.encode("user123"))
                    .role(UserRole.USER)
                    .enabled(true)
                    .storageQuota(1073741824L) // 1GB
                    .build();
            
            userRepository.save(user);
            log.info("Default test user created: user/user123");
        }
    }
}
