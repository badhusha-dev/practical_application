package com.example.graphql;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@Slf4j
public class GraphqlAdvancedDemoApplication {
    
    public static void main(String[] args) {
        log.info("Starting GraphQL Advanced Demo Application...");
        SpringApplication.run(GraphqlAdvancedDemoApplication.class, args);
        log.info("GraphQL Advanced Demo Application started successfully!");
        log.info("Application features:");
        log.info("- GraphQL API with queries, mutations, and subscriptions");
        log.info("- Redis caching for frequently requested data");
        log.info("- JWT authentication for protected operations");
        log.info("- DataLoader for efficient batch loading");
        log.info("- PostgreSQL for persistent data storage");
        log.info("- Flyway for database migrations");
        log.info("- Spring Boot Actuator for monitoring");
        log.info("");
        log.info("GraphQL Playground available at: http://localhost:8080/playground");
        log.info("GraphiQL available at: http://localhost:8080/graphiql");
        log.info("GraphQL endpoint: http://localhost:8080/graphql");
        log.info("Actuator endpoints: http://localhost:8080/actuator");
    }
}
