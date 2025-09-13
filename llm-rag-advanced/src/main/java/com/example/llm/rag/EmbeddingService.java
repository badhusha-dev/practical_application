package com.example.llm.rag;

import com.example.llm.llm.LlmProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {
    
    private final LlmProvider llmProvider;
    
    @Value("${app.rag.topK:6}")
    private int defaultTopK;
    
    public Mono<float[]> embedText(String text) {
        log.debug("Generating embedding for text length: {}", text.length());
        
        return llmProvider.embed(text)
                .doOnSuccess(embedding -> log.debug("Generated embedding with {} dimensions", embedding.length))
                .doOnError(error -> log.error("Failed to generate embedding", error));
    }
    
    public Mono<List<float[]>> embedTexts(List<String> texts) {
        log.debug("Generating embeddings for {} texts", texts.size());
        
        return llmProvider.embed(texts)
                .doOnSuccess(embeddings -> log.debug("Generated {} embeddings", embeddings.size()))
                .doOnError(error -> log.error("Failed to generate batch embeddings", error));
    }
    
    public String vectorToString(float[] vector) {
        if (vector == null || vector.length == 0) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < vector.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(String.format("%.6f", vector[i]));
        }
        sb.append("]");
        return sb.toString();
    }
    
    public float[] stringToVector(String vectorString) {
        if (vectorString == null || vectorString.isEmpty() || vectorString.equals("[]")) {
            return new float[0];
        }
        
        try {
            // Remove brackets and split by comma
            String cleanString = vectorString.replaceAll("[\\[\\]]", "");
            String[] parts = cleanString.split(",");
            
            float[] vector = new float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                vector[i] = Float.parseFloat(parts[i].trim());
            }
            return vector;
        } catch (Exception e) {
            log.error("Failed to parse vector string: {}", vectorString, e);
            return new float[0];
        }
    }
    
    public Mono<float[]> embedQuery(String query) {
        return embedText(query);
    }
}
