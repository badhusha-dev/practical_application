package com.example.llm.rag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class TextSplitter {
    
    private final Tika tika;
    
    @Value("${app.rag.chunkSize:3000}")
    private int chunkSize;
    
    @Value("${app.rag.chunkOverlap:200}")
    private int chunkOverlap;
    
    private static final Pattern SENTENCE_PATTERN = Pattern.compile(
            "[.!?]+\\s+", Pattern.MULTILINE
    );
    
    private static final Pattern PARAGRAPH_PATTERN = Pattern.compile(
            "\\n\\s*\\n", Pattern.MULTILINE
    );
    
    public String extractTextFromFile(byte[] fileContent, String contentType) {
        try {
            return tika.parseToString(new ByteArrayInputStream(fileContent));
        } catch (IOException | TikaException e) {
            log.error("Failed to extract text from file", e);
            throw new RuntimeException("Failed to extract text from file", e);
        }
    }
    
    public List<TextChunk> splitText(String text) {
        log.debug("Splitting text of length: {} into chunks", text.length());
        
        List<TextChunk> chunks = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return chunks;
        }
        
        // First try to split by paragraphs
        String[] paragraphs = PARAGRAPH_PATTERN.split(text);
        
        for (String paragraph : paragraphs) {
            paragraph = paragraph.trim();
            if (paragraph.isEmpty()) continue;
            
            if (paragraph.length() <= chunkSize) {
                chunks.add(new TextChunk(paragraph, 0));
            } else {
                // Split long paragraphs by sentences
                chunks.addAll(splitBySentences(paragraph));
            }
        }
        
        // If no paragraphs found, split the entire text
        if (chunks.isEmpty()) {
            chunks.addAll(splitBySentences(text));
        }
        
        // Merge small chunks and handle overlap
        chunks = mergeAndOverlapChunks(chunks);
        
        log.debug("Created {} chunks from text", chunks.size());
        return chunks;
    }
    
    private List<TextChunk> splitBySentences(String text) {
        List<TextChunk> chunks = new ArrayList<>();
        String[] sentences = SENTENCE_PATTERN.split(text);
        
        StringBuilder currentChunk = new StringBuilder();
        int chunkIndex = 0;
        
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty()) continue;
            
            if (currentChunk.length() + sentence.length() + 1 <= chunkSize) {
                if (currentChunk.length() > 0) {
                    currentChunk.append(" ");
                }
                currentChunk.append(sentence);
            } else {
                if (currentChunk.length() > 0) {
                    chunks.add(new TextChunk(currentChunk.toString(), chunkIndex++));
                    currentChunk = new StringBuilder();
                }
                
                // If single sentence is too long, split it by words
                if (sentence.length() > chunkSize) {
                    chunks.addAll(splitByWords(sentence, chunkIndex++));
                } else {
                    currentChunk.append(sentence);
                }
            }
        }
        
        if (currentChunk.length() > 0) {
            chunks.add(new TextChunk(currentChunk.toString(), chunkIndex));
        }
        
        return chunks;
    }
    
    private List<TextChunk> splitByWords(String text, int startIndex) {
        List<TextChunk> chunks = new ArrayList<>();
        String[] words = text.split("\\s+");
        
        StringBuilder currentChunk = new StringBuilder();
        int chunkIndex = startIndex;
        
        for (String word : words) {
            if (currentChunk.length() + word.length() + 1 <= chunkSize) {
                if (currentChunk.length() > 0) {
                    currentChunk.append(" ");
                }
                currentChunk.append(word);
            } else {
                if (currentChunk.length() > 0) {
                    chunks.add(new TextChunk(currentChunk.toString(), chunkIndex++));
                    currentChunk = new StringBuilder(word);
                } else {
                    // Single word is too long, truncate it
                    chunks.add(new TextChunk(word.substring(0, Math.min(word.length(), chunkSize)), chunkIndex++));
                }
            }
        }
        
        if (currentChunk.length() > 0) {
            chunks.add(new TextChunk(currentChunk.toString(), chunkIndex));
        }
        
        return chunks;
    }
    
    private List<TextChunk> mergeAndOverlapChunks(List<TextChunk> chunks) {
        List<TextChunk> mergedChunks = new ArrayList<>();
        
        for (int i = 0; i < chunks.size(); i++) {
            TextChunk chunk = chunks.get(i);
            
            // If chunk is too small and not the last one, try to merge with next
            if (chunk.getText().length() < chunkSize / 2 && i < chunks.size() - 1) {
                TextChunk nextChunk = chunks.get(i + 1);
                String mergedText = chunk.getText() + " " + nextChunk.getText();
                
                if (mergedText.length() <= chunkSize) {
                    mergedChunks.add(new TextChunk(mergedText, chunk.getIndex()));
                    i++; // Skip next chunk as it's been merged
                    continue;
                }
            }
            
            mergedChunks.add(chunk);
        }
        
        // Add overlap between chunks
        List<TextChunk> overlappedChunks = new ArrayList<>();
        for (int i = 0; i < mergedChunks.size(); i++) {
            TextChunk chunk = mergedChunks.get(i);
            
            if (i > 0 && chunkOverlap > 0) {
                TextChunk prevChunk = mergedChunks.get(i - 1);
                String prevText = prevChunk.getText();
                
                if (prevText.length() > chunkOverlap) {
                    String overlapText = prevText.substring(prevText.length() - chunkOverlap);
                    String overlappedText = overlapText + " " + chunk.getText();
                    overlappedChunks.add(new TextChunk(overlappedText, chunk.getIndex()));
                } else {
                    overlappedChunks.add(chunk);
                }
            } else {
                overlappedChunks.add(chunk);
            }
        }
        
        return overlappedChunks;
    }
    
    public static class TextChunk {
        private final String text;
        private final int index;
        
        public TextChunk(String text, int index) {
            this.text = text;
            this.index = index;
        }
        
        public String getText() {
            return text;
        }
        
        public int getIndex() {
            return index;
        }
        
        @Override
        public String toString() {
            return "TextChunk{index=" + index + ", textLength=" + text.length() + "}";
        }
    }
}
