package com.example.llm.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OllamaProvider implements LlmProvider {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${app.ollama.baseUrl}")
    private String baseUrl;
    
    @Value("${app.ollama.model}")
    private String model;
    
    @Value("${app.ollama.embeddingModel}")
    private String embeddingModel;
    
    @Value("${app.ollama.temperature:0.7}")
    private Double temperature;
    
    @Value("${app.ollama.maxTokens:2000}")
    private Integer maxTokens;
    
    @Override
    public Mono<Flux<String>> chatStream(ChatPrompt prompt) {
        log.debug("Ollama streaming chat with model: {}", model);
        
        Map<String, Object> requestBody = buildChatRequest(prompt, true);
        
        return webClient.post()
                .uri(baseUrl + "/api/generate")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToFlux(String.class)
                .map(this::parseStreamResponse)
                .flatMapMany(Flux::fromIterable)
                .map(this::extractToken)
                .filter(token -> token != null && !token.isEmpty())
                .collectList()
                .map(Flux::fromIterable);
    }
    
    @Override
    public Mono<String> chat(ChatPrompt prompt) {
        log.debug("Ollama chat with model: {}", model);
        
        Map<String, Object> requestBody = buildChatRequest(prompt, false);
        
        return webClient.post()
                .uri(baseUrl + "/api/generate")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseChatResponse);
    }
    
    @Override
    public Mono<float[]> embed(String text) {
        log.debug("Ollama embedding for text length: {}", text.length());
        
        Map<String, Object> requestBody = Map.of(
                "model", embeddingModel,
                "prompt", text
        );
        
        return webClient.post()
                .uri(baseUrl + "/api/embeddings")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseEmbeddingResponse);
    }
    
    @Override
    public Mono<List<float[]>> embed(List<String> texts) {
        log.debug("Ollama batch embedding for {} texts", texts.size());
        
        // Ollama doesn't support batch embeddings, so we'll do them one by one
        return Flux.fromIterable(texts)
                .flatMap(this::embed)
                .collectList();
    }
    
    @Override
    public String getModelName() {
        return model;
    }
    
    @Override
    public Mono<Boolean> isAvailable() {
        return webClient.get()
                .uri(baseUrl + "/api/tags")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> response.contains(model))
                .onErrorReturn(false);
    }
    
    private Map<String, Object> buildChatRequest(ChatPrompt prompt, boolean stream) {
        StringBuilder fullPrompt = new StringBuilder();
        
        // Add system prompt
        if (prompt.getSystemPrompt() != null && !prompt.getSystemPrompt().isEmpty()) {
            fullPrompt.append("System: ").append(prompt.getSystemPrompt()).append("\n\n");
        }
        
        // Add context if available
        if (prompt.getContext() != null && !prompt.getContext().isEmpty()) {
            fullPrompt.append("Context:\n").append(prompt.getContext()).append("\n\n");
        }
        
        // Add history
        if (prompt.getHistory() != null) {
            for (ChatMessage msg : prompt.getHistory()) {
                fullPrompt.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
            }
        }
        
        // Add user message
        fullPrompt.append("Human: ").append(prompt.getUserMessage()).append("\n\nAssistant:");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("prompt", fullPrompt.toString());
        requestBody.put("stream", stream);
        requestBody.put("options", Map.of(
                "temperature", prompt.getTemperature() != null ? prompt.getTemperature() : temperature,
                "num_predict", prompt.getMaxTokens() != null ? prompt.getMaxTokens() : maxTokens
        ));
        
        return requestBody;
    }
    
    private List<String> parseStreamResponse(String response) {
        List<String> tokens = new ArrayList<>();
        try {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                
                JsonNode jsonNode = objectMapper.readTree(line);
                if (jsonNode.has("response")) {
                    tokens.add(jsonNode.get("response").asText());
                }
                if (jsonNode.has("done") && jsonNode.get("done").asBoolean()) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing Ollama stream response", e);
        }
        return tokens;
    }
    
    private String extractToken(String token) {
        return token;
    }
    
    private String parseChatResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.has("response")) {
                return jsonNode.get("response").asText();
            }
        } catch (Exception e) {
            log.error("Error parsing Ollama chat response", e);
        }
        return "";
    }
    
    private float[] parseEmbeddingResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            if (jsonNode.has("embedding")) {
                JsonNode embedding = jsonNode.get("embedding");
                if (embedding.isArray()) {
                    float[] result = new float[embedding.size()];
                    for (int i = 0; i < embedding.size(); i++) {
                        result[i] = embedding.get(i).floatValue();
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing Ollama embedding response", e);
        }
        return new float[0];
    }
}
