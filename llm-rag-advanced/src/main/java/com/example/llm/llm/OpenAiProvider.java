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
public class OpenAiProvider implements LlmProvider {
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${app.openai.apiKey}")
    private String apiKey;
    
    @Value("${app.openai.model}")
    private String model;
    
    @Value("${app.openai.baseUrl}")
    private String baseUrl;
    
    @Value("${app.openai.embeddingModel}")
    private String embeddingModel;
    
    @Value("${app.openai.temperature:0.7}")
    private Double temperature;
    
    @Value("${app.openai.maxTokens:2000}")
    private Integer maxTokens;
    
    @Override
    public Mono<Flux<String>> chatStream(ChatPrompt prompt) {
        log.debug("OpenAI streaming chat with model: {}", model);
        
        Map<String, Object> requestBody = buildChatRequest(prompt, true);
        
        return webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
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
        log.debug("OpenAI chat with model: {}", model);
        
        Map<String, Object> requestBody = buildChatRequest(prompt, false);
        
        return webClient.post()
                .uri(baseUrl + "/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseChatResponse);
    }
    
    @Override
    public Mono<float[]> embed(String text) {
        log.debug("OpenAI embedding for text length: {}", text.length());
        
        Map<String, Object> requestBody = Map.of(
                "model", embeddingModel,
                "input", text
        );
        
        return webClient.post()
                .uri(baseUrl + "/embeddings")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseEmbeddingResponse);
    }
    
    @Override
    public Mono<List<float[]>> embed(List<String> texts) {
        log.debug("OpenAI batch embedding for {} texts", texts.size());
        
        Map<String, Object> requestBody = Map.of(
                "model", embeddingModel,
                "input", texts
        );
        
        return webClient.post()
                .uri(baseUrl + "/embeddings")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseBatchEmbeddingResponse);
    }
    
    @Override
    public String getModelName() {
        return model;
    }
    
    @Override
    public Mono<Boolean> isAvailable() {
        return webClient.get()
                .uri(baseUrl + "/models")
                .header("Authorization", "Bearer " + apiKey)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> response.contains(model))
                .onErrorReturn(false);
    }
    
    private Map<String, Object> buildChatRequest(ChatPrompt prompt, boolean stream) {
        List<Map<String, Object>> messages = new ArrayList<>();
        
        // Add system prompt
        if (prompt.getSystemPrompt() != null && !prompt.getSystemPrompt().isEmpty()) {
            messages.add(Map.of("role", "system", "content", prompt.getSystemPrompt()));
        }
        
        // Add context if available
        if (prompt.getContext() != null && !prompt.getContext().isEmpty()) {
            String contextMessage = prompt.getSystemPrompt() + "\n\nContext:\n" + prompt.getContext();
            messages.add(Map.of("role", "system", "content", contextMessage));
        }
        
        // Add history
        if (prompt.getHistory() != null) {
            for (ChatMessage msg : prompt.getHistory()) {
                Map<String, Object> message = new HashMap<>();
                message.put("role", msg.getRole());
                message.put("content", msg.getContent());
                if (msg.getName() != null) {
                    message.put("name", msg.getName());
                }
                messages.add(message);
            }
        }
        
        // Add user message
        messages.add(Map.of("role", "user", "content", prompt.getUserMessage()));
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", prompt.getTemperature() != null ? prompt.getTemperature() : temperature);
        requestBody.put("max_tokens", prompt.getMaxTokens() != null ? prompt.getMaxTokens() : maxTokens);
        requestBody.put("stream", stream);
        
        // Add tools if available
        if (prompt.getTools() != null && !prompt.getTools().isEmpty()) {
            requestBody.put("tools", convertToolsToOpenAIFormat(prompt.getTools()));
            requestBody.put("tool_choice", "auto");
        }
        
        return requestBody;
    }
    
    private List<Map<String, Object>> convertToolsToOpenAIFormat(List<ToolDefinition> tools) {
        List<Map<String, Object>> openAITools = new ArrayList<>();
        for (ToolDefinition tool : tools) {
            Map<String, Object> openAITool = Map.of(
                    "type", "function",
                    "function", Map.of(
                            "name", tool.getName(),
                            "description", tool.getDescription(),
                            "parameters", tool.getParameters()
                    )
            );
            openAITools.add(openAITool);
        }
        return openAITools;
    }
    
    private List<String> parseStreamResponse(String response) {
        List<String> tokens = new ArrayList<>();
        try {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6);
                    if (data.equals("[DONE]")) {
                        break;
                    }
                    JsonNode jsonNode = objectMapper.readTree(data);
                    JsonNode choices = jsonNode.get("choices");
                    if (choices != null && choices.isArray() && choices.size() > 0) {
                        JsonNode delta = choices.get(0).get("delta");
                        if (delta != null && delta.has("content")) {
                            tokens.add(delta.get("content").asText());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing stream response", e);
        }
        return tokens;
    }
    
    private String extractToken(String token) {
        return token;
    }
    
    private String parseChatResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode choices = jsonNode.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                return choices.get(0).get("message").get("content").asText();
            }
        } catch (Exception e) {
            log.error("Error parsing chat response", e);
        }
        return "";
    }
    
    private float[] parseEmbeddingResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode data = jsonNode.get("data");
            if (data != null && data.isArray() && data.size() > 0) {
                JsonNode embedding = data.get(0).get("embedding");
                if (embedding != null && embedding.isArray()) {
                    float[] result = new float[embedding.size()];
                    for (int i = 0; i < embedding.size(); i++) {
                        result[i] = embedding.get(i).floatValue();
                    }
                    return result;
                }
            }
        } catch (Exception e) {
            log.error("Error parsing embedding response", e);
        }
        return new float[0];
    }
    
    private List<float[]> parseBatchEmbeddingResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode data = jsonNode.get("data");
            if (data != null && data.isArray()) {
                List<float[]> result = new ArrayList<>();
                for (JsonNode item : data) {
                    JsonNode embedding = item.get("embedding");
                    if (embedding != null && embedding.isArray()) {
                        float[] embeddingArray = new float[embedding.size()];
                        for (int i = 0; i < embedding.size(); i++) {
                            embeddingArray[i] = embedding.get(i).floatValue();
                        }
                        result.add(embeddingArray);
                    }
                }
                return result;
            }
        } catch (Exception e) {
            log.error("Error parsing batch embedding response", e);
        }
        return new ArrayList<>();
    }
}
