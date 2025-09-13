package com.example.llm.controller;

import com.example.llm.dto.DocumentDTO;
import com.example.llm.dto.IngestRequest;
import com.example.llm.dto.SearchResultDTO;
import com.example.llm.entity.User;
import com.example.llm.rag.RetrieverService;
import com.example.llm.service.IngestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class IngestController {
    
    private final IngestService ingestService;
    private final RetrieverService retrieverService;
    
    @PostMapping("/documents")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "tags", required = false) String tagsJson,
            @AuthenticationPrincipal User user) {
        
        log.info("Document upload request from user: {} for file: {}", user.getUsername(), file.getOriginalFilename());
        
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            if (file.getSize() > 50 * 1024 * 1024) { // 50MB limit
                return ResponseEntity.badRequest().build();
            }
            
            List<String> tags = parseTags(tagsJson);
            DocumentDTO document = ingestService.ingestDocument(file, tags, user);
            
            return ResponseEntity.ok(document);
            
        } catch (Exception e) {
            log.error("Error uploading document", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<SearchResultDTO> search(@RequestParam("q") String query,
                                                 @RequestParam(value = "topK", required = false) Integer topK,
                                                 @AuthenticationPrincipal User user) {
        
        log.info("Search request from user: {} for query: {}", user.getUsername(), query);
        
        try {
            long startTime = System.currentTimeMillis();
            
            List<RetrieverService.ContextSnippet> snippets = retrieverService.retrieveContext(query, topK);
            
            long searchTime = System.currentTimeMillis() - startTime;
            
            SearchResultDTO result = new SearchResultDTO(
                    snippets.stream()
                            .map(snippet -> new com.example.llm.dto.ChunkDTO(
                                    snippet.getId(),
                                    snippet.getDocumentId(),
                                    snippet.getChunkIndex(),
                                    snippet.getText(),
                                    snippet.getMetadata(),
                                    snippet.getScore()
                            ))
                            .collect(java.util.stream.Collectors.toList()),
                    query,
                    snippets.size(),
                    searchTime
            );
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error searching", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDTO>> getUserDocuments(@AuthenticationPrincipal User user) {
        log.info("Get documents request from user: {}", user.getUsername());
        
        try {
            List<DocumentDTO> documents = ingestService.getUserDocuments(user);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Error getting user documents", e);
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/documents/{documentId}")
    public ResponseEntity<DocumentDTO> getDocument(@PathVariable UUID documentId,
                                                   @AuthenticationPrincipal User user) {
        log.info("Get document request from user: {} for document: {}", user.getUsername(), documentId);
        
        try {
            DocumentDTO document = ingestService.getDocument(documentId);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            log.error("Error getting document", e);
            return ResponseEntity.status(404).build();
        }
    }
    
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID documentId,
                                               @AuthenticationPrincipal User user) {
        log.info("Delete document request from user: {} for document: {}", user.getUsername(), documentId);
        
        try {
            ingestService.deleteDocument(documentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting document", e);
            return ResponseEntity.status(404).build();
        }
    }
    
    private List<String> parseTags(String tagsJson) {
        if (tagsJson == null || tagsJson.trim().isEmpty()) {
            return List.of();
        }
        
        try {
            return com.fasterxml.jackson.databind.ObjectMapper().readValue(tagsJson, List.class);
        } catch (Exception e) {
            log.warn("Failed to parse tags JSON: {}", tagsJson, e);
            return List.of();
        }
    }
}
