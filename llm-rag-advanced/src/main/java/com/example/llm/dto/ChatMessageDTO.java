package com.example.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    
    private UUID id;
    private UUID sessionId;
    private String role;
    private String content;
    private Integer tokensIn;
    private Integer tokensOut;
    private OffsetDateTime createdAt;
}
