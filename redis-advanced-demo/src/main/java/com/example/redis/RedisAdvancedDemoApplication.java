package com.example.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@SpringBootApplication
@EnableCaching
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1800) // 30 minutes
public class RedisAdvancedDemoApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisAdvancedDemoApplication.class);
    
    public static void main(String[] args) {
        logger.info("Starting Redis Advanced Demo Application...");
        SpringApplication.run(RedisAdvancedDemoApplication.class, args);
        logger.info("Redis Advanced Demo Application started successfully!");
        logger.info("Application features:");
        logger.info("- Redis caching for product queries");
        logger.info("- Redis-backed session management");
        logger.info("- Redis-based rate limiting");
        logger.info("- Redis Pub/Sub for real-time notifications");
        logger.info("- PostgreSQL for persistent data storage");
    }
}
