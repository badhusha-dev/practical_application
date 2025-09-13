package com.example.llm.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class ToolCallParser {
    
    private final ObjectMapper objectMapper;
    
    // Pattern to match tool calls in the format: <tool_call>{"name": "tool_name", "args": {...}}</tool_call>
    private static final Pattern TOOL_CALL_PATTERN = Pattern.compile(
            "<tool_call>\\s*\\{.*?\\}\\s*</tool_call>", 
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );
    
    public List<ToolCall> parseToolCalls(String content) {
        List<ToolCall> toolCalls = new ArrayList<>();
        
        if (content == null || content.isEmpty()) {
            return toolCalls;
        }
        
        Matcher matcher = TOOL_CALL_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String toolCallJson = matcher.group();
            // Remove the XML-like tags
            String jsonContent = toolCallJson
                    .replaceAll("</?tool_call>", "")
                    .trim();
            
            try {
                JsonNode jsonNode = objectMapper.readTree(jsonContent);
                String name = jsonNode.get("name").asText();
                JsonNode argsNode = jsonNode.get("args");
                
                ToolCall toolCall = new ToolCall(name, argsNode.toString());
                toolCalls.add(toolCall);
                
                log.debug("Parsed tool call: {} with args: {}", name, argsNode.toString());
            } catch (Exception e) {
                log.warn("Failed to parse tool call JSON: {}", jsonContent, e);
            }
        }
        
        return toolCalls;
    }
    
    public static class ToolCall {
        private final String name;
        private final String args;
        
        public ToolCall(String name, String args) {
            this.name = name;
            this.args = args;
        }
        
        public String getName() {
            return name;
        }
        
        public String getArgs() {
            return args;
        }
        
        @Override
        public String toString() {
            return "ToolCall{name='" + name + "', args='" + args + "'}";
        }
    }
}
