package com.example.llm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {
    
    private List<ChunkDTO> chunks;
    private String query;
    private Integer totalResults;
    private Long searchTimeMs;
}
