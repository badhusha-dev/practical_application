package com.example.llm.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RerankerService {
    
    public List<RetrieverService.ContextSnippet> rerank(List<RetrieverService.ContextSnippet> snippets, String query) {
        log.debug("Reranking {} snippets for query: {}", snippets.size(), query);
        
        if (snippets.size() <= 1) {
            return snippets;
        }
        
        // Simple cosine similarity reranking
        List<ScoredSnippet> scoredSnippets = new ArrayList<>();
        
        for (RetrieverService.ContextSnippet snippet : snippets) {
            double score = calculateRelevanceScore(snippet, query);
            scoredSnippets.add(new ScoredSnippet(snippet, score));
        }
        
        // Sort by relevance score (descending)
        scoredSnippets.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        return scoredSnippets.stream()
                .map(ScoredSnippet::getSnippet)
                .collect(java.util.stream.Collectors.toList());
    }
    
    private double calculateRelevanceScore(RetrieverService.ContextSnippet snippet, String query) {
        String text = snippet.getText().toLowerCase();
        String queryLower = query.toLowerCase();
        
        // Simple keyword matching score
        double keywordScore = 0.0;
        String[] queryWords = queryLower.split("\\s+");
        
        for (String word : queryWords) {
            if (word.length() > 2) { // Ignore short words
                int occurrences = countOccurrences(text, word);
                keywordScore += occurrences * (1.0 / word.length()); // Longer words get less weight
            }
        }
        
        // Normalize by text length
        keywordScore = keywordScore / Math.max(text.length() / 100.0, 1.0);
        
        // Combine with original similarity score
        double originalScore = snippet.getScore() != null ? snippet.getScore() : 0.0;
        
        return 0.7 * originalScore + 0.3 * keywordScore;
    }
    
    private int countOccurrences(String text, String word) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(word, index)) != -1) {
            count++;
            index += word.length();
        }
        return count;
    }
    
    private static class ScoredSnippet {
        private final RetrieverService.ContextSnippet snippet;
        private final double score;
        
        public ScoredSnippet(RetrieverService.ContextSnippet snippet, double score) {
            this.snippet = snippet;
            this.score = score;
        }
        
        public RetrieverService.ContextSnippet getSnippet() {
            return snippet;
        }
        
        public double getScore() {
            return score;
        }
    }
}
