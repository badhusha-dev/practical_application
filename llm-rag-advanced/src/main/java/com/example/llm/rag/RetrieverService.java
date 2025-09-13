package com.example.llm.rag;

import com.example.llm.dto.ChunkDTO;
import com.example.llm.entity.Chunk;
import com.example.llm.entity.Document;
import com.example.llm.repository.ChunkRepository;
import com.example.llm.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RetrieverService {
    
    private final ChunkRepository chunkRepository;
    private final DocumentRepository documentRepository;
    private final EmbeddingService embeddingService;
    
    @Value("${app.rag.topK:6}")
    private int defaultTopK;
    
    @Value("${app.rag.maxContextChars:10000}")
    private int maxContextChars;
    
    @Cacheable(value = "searchResults", key = "#query + '_' + #topK")
    public List<ContextSnippet> retrieveContext(String query, Integer topK) {
        log.debug("Retrieving context for query: {} with topK: {}", query, topK);
        
        int k = topK != null ? topK : defaultTopK;
        
        return embeddingService.embedQuery(query)
                .map(embedding -> {
                    String vectorString = embeddingService.vectorToString(embedding);
                    
                    // Get similar chunks from database
                    List<Object[]> results = chunkRepository.findSimilarChunks(vectorString, k * 2); // Get more for reranking
                    
                    // Convert to ContextSnippet objects
                    List<ContextSnippet> snippets = results.stream()
                            .map(this::convertToContextSnippet)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    
                    // Apply MMR reranking
                    List<ContextSnippet> rerankedSnippets = applyMMRReranking(snippets, k);
                    
                    // Limit context size
                    return limitContextSize(rerankedSnippets);
                })
                .block(); // Convert Mono to blocking call for now
    }
    
    public List<ContextSnippet> retrieveContextByDocument(String query, UUID documentId, Integer topK) {
        log.debug("Retrieving context for query: {} in document: {} with topK: {}", query, documentId, topK);
        
        int k = topK != null ? topK : defaultTopK;
        
        return embeddingService.embedQuery(query)
                .map(embedding -> {
                    String vectorString = embeddingService.vectorToString(embedding);
                    
                    // Get similar chunks from specific document
                    List<Object[]> results = chunkRepository.findSimilarChunksByDocument(vectorString, documentId, k);
                    
                    // Convert to ContextSnippet objects
                    return results.stream()
                            .map(this::convertToContextSnippet)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                })
                .block();
    }
    
    private ContextSnippet convertToContextSnippet(Object[] result) {
        try {
            UUID id = (UUID) result[0];
            UUID documentId = (UUID) result[1];
            Integer chunkIndex = (Integer) result[2];
            String text = (String) result[3];
            String metadata = (String) result[4];
            Double score = ((Number) result[5]).doubleValue();
            
            // Get document info
            Document document = documentRepository.findById(documentId).orElse(null);
            String documentName = document != null ? document.getFilename() : "Unknown";
            
            return new ContextSnippet(id, documentId, documentName, chunkIndex, text, metadata, score);
        } catch (Exception e) {
            log.error("Failed to convert result to ContextSnippet", e);
            return null;
        }
    }
    
    private List<ContextSnippet> applyMMRReranking(List<ContextSnippet> snippets, int k) {
        if (snippets.size() <= k) {
            return snippets;
        }
        
        List<ContextSnippet> selected = new ArrayList<>();
        List<ContextSnippet> remaining = new ArrayList<>(snippets);
        
        // Select first snippet (highest similarity)
        if (!remaining.isEmpty()) {
            selected.add(remaining.remove(0));
        }
        
        // Apply MMR for remaining slots
        while (selected.size() < k && !remaining.isEmpty()) {
            ContextSnippet bestSnippet = null;
            double bestScore = -1;
            int bestIndex = -1;
            
            for (int i = 0; i < remaining.size(); i++) {
                ContextSnippet snippet = remaining.get(i);
                
                // Calculate MMR score: λ * similarity - (1-λ) * max_relevance_to_selected
                double maxRelevance = calculateMaxRelevance(snippet, selected);
                double mmrScore = 0.7 * snippet.getScore() - 0.3 * maxRelevance;
                
                if (mmrScore > bestScore) {
                    bestScore = mmrScore;
                    bestSnippet = snippet;
                    bestIndex = i;
                }
            }
            
            if (bestSnippet != null) {
                selected.add(bestSnippet);
                remaining.remove(bestIndex);
            } else {
                break;
            }
        }
        
        return selected;
    }
    
    private double calculateMaxRelevance(ContextSnippet snippet, List<ContextSnippet> selected) {
        if (selected.isEmpty()) {
            return 0.0;
        }
        
        double maxRelevance = 0.0;
        for (ContextSnippet selectedSnippet : selected) {
            // Simple relevance based on document and chunk proximity
            double relevance = 0.0;
            
            if (snippet.getDocumentId().equals(selectedSnippet.getDocumentId())) {
                relevance += 0.5; // Same document
                
                // Chunk proximity
                int chunkDiff = Math.abs(snippet.getChunkIndex() - selectedSnippet.getChunkIndex());
                if (chunkDiff <= 2) {
                    relevance += 0.3;
                } else if (chunkDiff <= 5) {
                    relevance += 0.1;
                }
            }
            
            maxRelevance = Math.max(maxRelevance, relevance);
        }
        
        return maxRelevance;
    }
    
    private List<ContextSnippet> limitContextSize(List<ContextSnippet> snippets) {
        List<ContextSnippet> limitedSnippets = new ArrayList<>();
        int currentSize = 0;
        
        for (ContextSnippet snippet : snippets) {
            if (currentSize + snippet.getText().length() <= maxContextChars) {
                limitedSnippets.add(snippet);
                currentSize += snippet.getText().length();
            } else {
                // Truncate the last snippet if needed
                int remainingChars = maxContextChars - currentSize;
                if (remainingChars > 100) { // Only add if we have meaningful content
                    String truncatedText = snippet.getText().substring(0, remainingChars) + "...";
                    ContextSnippet truncatedSnippet = new ContextSnippet(
                            snippet.getId(),
                            snippet.getDocumentId(),
                            snippet.getDocumentName(),
                            snippet.getChunkIndex(),
                            truncatedText,
                            snippet.getMetadata(),
                            snippet.getScore()
                    );
                    limitedSnippets.add(truncatedSnippet);
                }
                break;
            }
        }
        
        return limitedSnippets;
    }
    
    public static class ContextSnippet {
        private final UUID id;
        private final UUID documentId;
        private final String documentName;
        private final Integer chunkIndex;
        private final String text;
        private final String metadata;
        private final Double score;
        
        public ContextSnippet(UUID id, UUID documentId, String documentName, Integer chunkIndex, 
                            String text, String metadata, Double score) {
            this.id = id;
            this.documentId = documentId;
            this.documentName = documentName;
            this.chunkIndex = chunkIndex;
            this.text = text;
            this.metadata = metadata;
            this.score = score;
        }
        
        public UUID getId() { return id; }
        public UUID getDocumentId() { return documentId; }
        public String getDocumentName() { return documentName; }
        public Integer getChunkIndex() { return chunkIndex; }
        public String getText() { return text; }
        public String getMetadata() { return metadata; }
        public Double getScore() { return score; }
        
        public String getSourceReference() {
            return String.format("[Source: %s#%d]", documentName, chunkIndex);
        }
        
        @Override
        public String toString() {
            return "ContextSnippet{document=" + documentName + ", chunk=" + chunkIndex + ", score=" + score + "}";
        }
    }
}
