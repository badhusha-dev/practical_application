package com.example.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    private UUID sessionId;
    private String message;
    private boolean useRag = true;
    private Integer topK;
    private List<String> tools;
}
