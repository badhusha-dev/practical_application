package com.example.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDTO {
    
    private UUID id;
    private UUID userId;
    private String title;
    private OffsetDateTime createdAt;
}
