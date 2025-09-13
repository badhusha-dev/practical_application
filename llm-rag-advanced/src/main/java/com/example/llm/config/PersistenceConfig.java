package com.example.llm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.llm.repository")
public class PersistenceConfig {
    
    // Additional JPA configuration can be added here if needed
    // The main configuration is in application.yml
}
