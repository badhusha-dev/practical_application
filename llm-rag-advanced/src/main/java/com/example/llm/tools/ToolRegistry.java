package com.example.llm.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolRegistry {
    
    private final ObjectMapper objectMapper;
    private final Map<String, Tool> tools = new HashMap<>();
    
    public void registerTool(Tool tool) {
        tools.put(tool.getName(), tool);
        log.info("Registered tool: {}", tool.getName());
    }
    
    public Tool getTool(String name) {
        return tools.get(name);
    }
    
    public Map<String, Tool> getAllTools() {
        return new HashMap<>(tools);
    }
    
    public CompletableFuture<String> invokeTool(String toolName, String argsJson) {
        Tool tool = getTool(toolName);
        if (tool == null) {
            log.warn("Tool not found: {}", toolName);
            return CompletableFuture.completedFuture("Tool not found: " + toolName);
        }
        
        try {
            JsonNode args = objectMapper.readTree(argsJson);
            return tool.invoke(args);
        } catch (Exception e) {
            log.error("Failed to invoke tool: {} with args: {}", toolName, argsJson, e);
            return CompletableFuture.completedFuture("Error invoking tool: " + e.getMessage());
        }
    }
    
    public interface Tool {
        String getName();
        String getDescription();
        Map<String, Object> getParameters();
        CompletableFuture<String> invoke(JsonNode args);
    }
}
