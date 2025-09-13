package com.example.thymeleafmongoflywayapp.config;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableMongock
public class MongockConfig {
    // Mongock configuration is handled by @EnableMongock annotation
    // Additional configuration can be added here if needed
}
