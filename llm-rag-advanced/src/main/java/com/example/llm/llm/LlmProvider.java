package com.example.llm.llm;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LlmProvider {
    
    /**
     * Stream chat completion tokens
     */
    Mono<Flux<String>> chatStream(ChatPrompt prompt);
    
    /**
     * Get complete chat response
     */
    Mono<String> chat(ChatPrompt prompt);
    
    /**
     * Generate embedding for a single text
     */
    Mono<float[]> embed(String text);
    
    /**
     * Generate embeddings for multiple texts
     */
    Mono<List<float[]>> embed(List<String> texts);
    
    /**
     * Get the model name being used
     */
    String getModelName();
    
    /**
     * Check if the provider is available
     */
    Mono<Boolean> isAvailable();
}
