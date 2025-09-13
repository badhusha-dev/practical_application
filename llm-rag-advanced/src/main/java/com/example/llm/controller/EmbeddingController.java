package com.example.llm.controller;

import com.example.llm.dto.EmbeddingRequest;
import com.example.llm.dto.EmbeddingResponse;
import com.example.llm.entity.User;
import com.example.llm.rag.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class EmbeddingController {
    
    private final EmbeddingService embeddingService;
    
    @PostMapping("/embeddings")
    public ResponseEntity<EmbeddingResponse> generateEmbeddings(@RequestBody EmbeddingRequest request,
                                                               @AuthenticationPrincipal User user) {
        log.info("Embedding request from user: {} for {} texts", user.getUsername(), request.getTexts().size());
        
        try {
            if (request.getTexts() == null || request.getTexts().isEmpty()) {
                return ResponseEntity.badRequest().body(new EmbeddingResponse(List.of(), "error", 0));
            }
            
            if (request.getTexts().size() > 100) {
                return ResponseEntity.badRequest().body(new EmbeddingResponse(List.of(), "error", 0));
            }
            
            List<float[]> embeddings = embeddingService.embedTexts(request.getTexts()).block();
            
            EmbeddingResponse response = new EmbeddingResponse(
                    embeddings,
                    "text-embedding-3-small", // Default model name
                    request.getTexts().size()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error generating embeddings", e);
            return ResponseEntity.status(500).body(new EmbeddingResponse(List.of(), "error", 0));
        }
    }
}
