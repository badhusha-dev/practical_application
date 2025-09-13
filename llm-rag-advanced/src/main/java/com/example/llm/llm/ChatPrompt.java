package com.example.llm.llm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatPrompt {
    
    private String systemPrompt;
    private String userMessage;
    private String context;
    private List<ChatMessage> history;
    private List<ToolDefinition> tools;
    private Double temperature;
    private Integer maxTokens;
}
