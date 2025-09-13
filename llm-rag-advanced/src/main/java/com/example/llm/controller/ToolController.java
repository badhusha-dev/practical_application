package com.example.llm.controller;

import com.example.llm.entity.User;
import com.example.llm.tools.ToolRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ToolController {
    
    private final ToolRegistry toolRegistry;
    
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> getAvailableTools(@AuthenticationPrincipal User user) {
        log.info("Get tools request from user: {}", user.getUsername());
        
        Map<String, Object> response = new HashMap<>();
        Map<String, ToolRegistry.Tool> tools = toolRegistry.getAllTools();
        
        Map<String, Object> toolDefinitions = new HashMap<>();
        for (Map.Entry<String, ToolRegistry.Tool> entry : tools.entrySet()) {
            ToolRegistry.Tool tool = entry.getValue();
            Map<String, Object> toolDef = new HashMap<>();
            toolDef.put("name", tool.getName());
            toolDef.put("description", tool.getDescription());
            toolDef.put("parameters", tool.getParameters());
            toolDefinitions.put(tool.getName(), toolDef);
        }
        
        response.put("tools", toolDefinitions);
        response.put("count", tools.size());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/tools/{toolName}/invoke")
    public ResponseEntity<Map<String, Object>> invokeTool(@PathVariable String toolName,
                                                         @RequestBody Map<String, Object> args,
                                                         @AuthenticationPrincipal User user) {
        log.info("Tool invocation request from user: {} for tool: {}", user.getUsername(), toolName);
        
        try {
            ToolRegistry.Tool tool = toolRegistry.getTool(toolName);
            if (tool == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Tool not found: " + toolName);
                return ResponseEntity.status(404).body(error);
            }
            
            String argsJson = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(args);
            String result = toolRegistry.invokeTool(toolName, argsJson).get();
            
            Map<String, Object> response = new HashMap<>();
            response.put("tool", toolName);
            response.put("args", args);
            response.put("result", result);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error invoking tool: {}", toolName, e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to invoke tool: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @GetMapping("/tools/weather/demo")
    public ResponseEntity<Map<String, Object>> weatherDemo(@AuthenticationPrincipal User user) {
        log.info("Weather demo request from user: {}", user.getUsername());
        
        Map<String, Object> demo = new HashMap<>();
        demo.put("tool", "weather.lookup");
        demo.put("description", "Get weather information for a city and date");
        demo.put("example", Map.of(
                "city", "London",
                "date", "2024-01-15"
        ));
        
        return ResponseEntity.ok(demo);
    }
    
    @GetMapping("/tools/math/demo")
    public ResponseEntity<Map<String, Object>> mathDemo(@AuthenticationPrincipal User user) {
        log.info("Math demo request from user: {}", user.getUsername());
        
        Map<String, Object> demo = new HashMap<>();
        demo.put("tool", "math.eval");
        demo.put("description", "Safely evaluate mathematical expressions");
        demo.put("example", Map.of(
                "expression", "2 + 3 * 4"
        ));
        
        return ResponseEntity.ok(demo);
    }
    
    @GetMapping("/tools/url/demo")
    public ResponseEntity<Map<String, Object>> urlDemo(@AuthenticationPrincipal User user) {
        log.info("URL demo request from user: {}", user.getUsername());
        
        Map<String, Object> demo = new HashMap<>();
        demo.put("tool", "url.summary");
        demo.put("description", "Fetch and summarize content from a URL");
        demo.put("example", Map.of(
                "url", "https://en.wikipedia.org/wiki/Artificial_intelligence"
        ));
        
        return ResponseEntity.ok(demo);
    }
}
