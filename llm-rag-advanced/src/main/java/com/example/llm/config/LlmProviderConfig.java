package com.example.llm.config;

import com.example.llm.llm.LlmProvider;
import com.example.llm.llm.OllamaProvider;
import com.example.llm.llm.OpenAiProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class LlmProviderConfig {
    
    @Value("${app.provider}")
    private String provider;
    
    @Bean
    @Primary
    public LlmProvider llmProvider(OpenAiProvider openAiProvider, OllamaProvider ollamaProvider) {
        log.info("Configuring LLM provider: {}", provider);
        
        switch (provider.toLowerCase()) {
            case "openai":
                log.info("Using OpenAI provider");
                return openAiProvider;
            case "ollama":
                log.info("Using Ollama provider");
                return ollamaProvider;
            default:
                log.warn("Unknown provider: {}, defaulting to OpenAI", provider);
                return openAiProvider;
        }
    }
}
