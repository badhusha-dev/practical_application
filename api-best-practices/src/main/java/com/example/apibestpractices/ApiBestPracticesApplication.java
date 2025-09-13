package com.example.apibestpractices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@Slf4j
public class ApiBestPracticesApplication {
    
    public static void main(String[] args) {
        log.info("Starting API Best Practices Application...");
        SpringApplication.run(ApiBestPracticesApplication.class, args);
        log.info("API Best Practices Application started successfully!");
    }
}
