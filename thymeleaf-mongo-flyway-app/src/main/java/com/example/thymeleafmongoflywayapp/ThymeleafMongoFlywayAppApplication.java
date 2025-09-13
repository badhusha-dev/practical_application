package com.example.thymeleafmongoflywayapp;

import com.example.thymeleafmongoflywayapp.entity.User;
import com.example.thymeleafmongoflywayapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class ThymeleafMongoFlywayAppApplication {

    private final UserService userService;

    public static void main(String[] args) {
        log.info("Starting Thymeleaf MongoDB Flyway Application...");
        SpringApplication.run(ThymeleafMongoFlywayAppApplication.class, args);
        log.info("Application started successfully!");
    }

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            log.info("Checking for existing users...");
            
            long userCount = userService.getUserCount();
            log.info("Current user count: {}", userCount);
            
            if (userCount == 0) {
                log.info("No users found. Creating seed data...");
                createSeedUsers();
                log.info("Seed data created successfully!");
            } else {
                log.info("Users already exist. Skipping seed data creation.");
            }
        };
    }

    private void createSeedUsers() {
        List<User> seedUsers = Arrays.asList(
            new User("John Doe", "john.doe@example.com", 28),
            new User("Jane Smith", "jane.smith@example.com", 32),
            new User("Mike Johnson", "mike.johnson@example.com", 25),
            new User("Sarah Wilson", "sarah.wilson@example.com", 29),
            new User("David Brown", "david.brown@example.com", 35),
            new User("Emily Davis", "emily.davis@example.com", 27),
            new User("Robert Miller", "robert.miller@example.com", 31),
            new User("Lisa Garcia", "lisa.garcia@example.com", 26),
            new User("Michael Martinez", "michael.martinez@example.com", 33),
            new User("Jennifer Anderson", "jennifer.anderson@example.com", 30)
        );

        for (User user : seedUsers) {
            try {
                userService.saveUser(user);
                log.debug("Created user: {} ({})", user.getName(), user.getEmail());
            } catch (Exception e) {
                log.warn("Failed to create user {}: {}", user.getEmail(), e.getMessage());
            }
        }
        
        log.info("Created {} seed users", seedUsers.size());
    }
}
