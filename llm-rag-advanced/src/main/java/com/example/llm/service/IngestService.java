package com.example.llm.service;

import com.example.llm.dto.DocumentDTO;
import com.example.llm.entity.Chunk;
import com.example.llm.entity.Document;
import com.example.llm.entity.User;
import com.example.llm.rag.EmbeddingService;
import com.example.llm.rag.TextSplitter;
import com.example.llm.repository.ChunkRepository;
import com.example.llm.repository.DocumentRepository;
import com.example.llm.util.HashingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IngestService {
    
    private final DocumentRepository documentRepository;
    private final ChunkRepository chunkRepository;
    private final TextSplitter textSplitter;
    private final EmbeddingService embeddingService;
    
    @Value("${app.rag.chunkSize:3000}")
    private int chunkSize;
    
    @CacheEvict(value = "searchResults", allEntries = true)
    public DocumentDTO ingestDocument(MultipartFile file, List<String> tags, User user) {
        log.info("Ingesting document: {} for user: {}", file.getOriginalFilename(), user.getUsername());
        
        try {
            // Check if document already exists
            String checksum = HashingUtils.calculateSHA256(file.getBytes());
            Document existingDocument = documentRepository.findByChecksum(checksum).orElse(null);
            
            if (existingDocument != null) {
                log.info("Document already exists with checksum: {}", checksum);
                return convertToDTO(existingDocument);
            }
            
            // Extract text from file
            String extractedText = textSplitter.extractTextFromFile(file.getBytes(), file.getContentType());
            log.debug("Extracted text length: {}", extractedText.length());
            
            // Split text into chunks
            List<TextSplitter.TextChunk> textChunks = textSplitter.splitText(extractedText);
            log.debug("Split text into {} chunks", textChunks.size());
            
            // Create document entity
            Document document = new Document(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    checksum
            );
            
            // Set tags
            if (tags != null && !tags.isEmpty()) {
                document.setTags(convertTagsToJson(tags));
            }
            
            Document savedDocument = documentRepository.save(document);
            log.debug("Saved document with ID: {}", savedDocument.getId());
            
            // Create chunks and generate embeddings
            List<CompletableFuture<Void>> embeddingFutures = new ArrayList<>();
            
            for (int i = 0; i < textChunks.size(); i++) {
                TextSplitter.TextChunk textChunk = textChunks.get(i);
                
                // Create chunk entity
                Chunk chunk = new Chunk(savedDocument.getId(), textChunk.getIndex(), textChunk.getText());
                Chunk savedChunk = chunkRepository.save(chunk);
                
                // Generate embedding asynchronously
                CompletableFuture<Void> embeddingFuture = embeddingService.embedText(textChunk.getText())
                        .thenAccept(embedding -> {
                            String vectorString = embeddingService.vectorToString(embedding);
                            savedChunk.setVector(vectorString);
                            chunkRepository.save(savedChunk);
                            log.debug("Generated embedding for chunk: {}", savedChunk.getId());
                        })
                        .exceptionally(throwable -> {
                            log.error("Failed to generate embedding for chunk: {}", savedChunk.getId(), throwable);
                            return null;
                        });
                
                embeddingFutures.add(embeddingFuture);
            }
            
            // Wait for all embeddings to complete
            CompletableFuture.allOf(embeddingFutures.toArray(new CompletableFuture[0])).join();
            
            log.info("Successfully ingested document: {} with {} chunks", file.getOriginalFilename(), textChunks.size());
            
            return convertToDTO(savedDocument);
            
        } catch (Exception e) {
            log.error("Failed to ingest document: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Failed to ingest document: " + e.getMessage(), e);
        }
    }
    
    public List<DocumentDTO> getUserDocuments(User user) {
        // For now, return all documents. In a real app, you might want to filter by user
        List<Document> documents = documentRepository.findAll();
        return documents.stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
    }
    
    public DocumentDTO getDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found: " + documentId));
        return convertToDTO(document);
    }
    
    public void deleteDocument(UUID documentId) {
        if (!documentRepository.existsById(documentId)) {
            throw new RuntimeException("Document not found: " + documentId);
        }
        
        // Chunks will be deleted automatically due to CASCADE
        documentRepository.deleteById(documentId);
        log.info("Deleted document: {}", documentId);
    }
    
    private DocumentDTO convertToDTO(Document document) {
        return new DocumentDTO(
                document.getId(),
                document.getFilename(),
                document.getContentType(),
                document.getSize(),
                document.getChecksum(),
                document.getTags(),
                document.getCreatedAt()
        );
    }
    
    private String convertTagsToJson(List<String> tags) {
        try {
            return com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(tags);
        } catch (Exception e) {
            log.error("Failed to convert tags to JSON", e);
            return "[]";
        }
    }
}
