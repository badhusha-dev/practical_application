package com.example.apibestpractices.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RateLimitConfig {
    
    @Value("${app.rate-limit.enabled:true}")
    private boolean rateLimitEnabled;
    
    @Value("${app.rate-limit.requests-per-minute:100}")
    private int requestsPerMinute;
    
    @Value("${app.rate-limit.burst-capacity:200}")
    private int burstCapacity;
    
    @Value("${app.rate-limit.refill-tokens:100}")
    private int refillTokens;
    
    @Value("${app.rate-limit.refill-period:60}")
    private int refillPeriodSeconds;
    
    @Bean
    @ConditionalOnProperty(name = "app.rate-limit.enabled", havingValue = "true", matchIfMissing = true)
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
