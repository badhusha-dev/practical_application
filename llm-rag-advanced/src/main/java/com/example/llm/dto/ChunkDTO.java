package com.example.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkDTO {
    
    private UUID id;
    private UUID documentId;
    private Integer chunkIndex;
    private String text;
    private String metadata;
    private Double score; // For similarity search results
}
