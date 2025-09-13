package com.example.llm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    
    private String provider;
    private OpenAiProperties openai = new OpenAiProperties();
    private OllamaProperties ollama = new OllamaProperties();
    private RagProperties rag = new RagProperties();
    private SecurityProperties security = new SecurityProperties();
    private RateLimitProperties rateLimit = new RateLimitProperties();
    private ToolsProperties tools = new ToolsProperties();
    
    @Data
    public static class OpenAiProperties {
        private String apiKey;
        private String model;
        private String baseUrl;
        private String embeddingModel;
        private Double temperature;
        private Integer maxTokens;
    }
    
    @Data
    public static class OllamaProperties {
        private String baseUrl;
        private String model;
        private String embeddingModel;
        private Double temperature;
        private Integer maxTokens;
    }
    
    @Data
    public static class RagProperties {
        private Integer topK;
        private Integer maxContextChars;
        private Integer chunkSize;
        private Integer chunkOverlap;
        private String systemPrompt;
    }
    
    @Data
    public static class SecurityProperties {
        private JwtProperties jwt = new JwtProperties();
        
        @Data
        public static class JwtProperties {
            private String secret;
            private Long expiration;
        }
    }
    
    @Data
    public static class RateLimitProperties {
        private Integer requestsPerMinute;
        private Integer windowSizeMinutes;
    }
    
    @Data
    public static class ToolsProperties {
        private Boolean enabled;
        private Integer maxToolCalls;
        private Integer toolTimeoutSeconds;
    }
}
