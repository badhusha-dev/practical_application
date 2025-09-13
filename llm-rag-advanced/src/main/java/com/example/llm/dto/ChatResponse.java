package com.example.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    private String type; // delta, done, tool_call, error
    private String token;
    private String content;
    private String toolName;
    private String toolArgs;
    private String error;
}
