package com.example.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    
    private UUID id;
    private String filename;
    private String contentType;
    private Long size;
    private String checksum;
    private String tags;
    private OffsetDateTime createdAt;
}
