package com.example.llm.tools;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Slf4j
public class UrlSummaryTool implements ToolRegistry.Tool {
    
    private final WebClient webClient;
    private final Tika tika;
    
    @Value("${app.tools.toolTimeoutSeconds:30}")
    private int toolTimeoutSeconds;
    
    private static final Pattern URL_PATTERN = Pattern.compile(
            "https?://[\\w\\-]+(\\.[\\w\\-]+)+([\\w\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?"
    );
    
    private static final int MAX_CONTENT_LENGTH = 5000;
    private static final int SUMMARY_LENGTH = 500;
    
    @Override
    public String getName() {
        return "url.summary";
    }
    
    @Override
    public String getDescription() {
        return "Fetch and summarize content from a URL";
    }
    
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");
        
        Map<String, Object> properties = new HashMap<>();
        
        Map<String, Object> urlParam = new HashMap<>();
        urlParam.put("type", "string");
        urlParam.put("description", "The URL to fetch and summarize");
        properties.put("url", urlParam);
        
        parameters.put("properties", properties);
        parameters.put("required", new String[]{"url"});
        
        return parameters;
    }
    
    @Override
    public CompletableFuture<String> invoke(JsonNode args) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String urlStr = args.get("url").asText().trim();
                
                log.debug("Fetching and summarizing URL: {}", urlStr);
                
                // Validate URL
                if (!isValidUrl(urlStr)) {
                    return "Error: Invalid URL format";
                }
                
                // Check for allowed domains (basic security)
                if (!isAllowedDomain(urlStr)) {
                    return "Error: Domain not allowed for security reasons";
                }
                
                // Fetch content
                String content = fetchUrlContent(urlStr);
                if (content == null || content.isEmpty()) {
                    return "Error: Could not fetch content from URL";
                }
                
                // Extract text using Tika
                String extractedText = extractTextFromHtml(content);
                if (extractedText == null || extractedText.isEmpty()) {
                    return "Error: Could not extract text from content";
                }
                
                // Truncate if too long
                if (extractedText.length() > MAX_CONTENT_LENGTH) {
                    extractedText = extractedText.substring(0, MAX_CONTENT_LENGTH) + "...";
                }
                
                // Generate summary
                String summary = generateSummary(extractedText);
                
                return String.format("Summary of %s:\n\n%s", urlStr, summary);
                
            } catch (Exception e) {
                log.error("Error in URL summary tool", e);
                return "Error summarizing URL: " + e.getMessage();
            }
        });
    }
    
    private boolean isValidUrl(String url) {
        try {
            new URL(url);
            return URL_PATTERN.matcher(url).matches();
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean isAllowedDomain(String url) {
        try {
            URL parsedUrl = new URL(url);
            String host = parsedUrl.getHost().toLowerCase();
            
            // Block potentially dangerous domains
            String[] blockedDomains = {
                    "localhost", "127.0.0.1", "0.0.0.0", "::1",
                    "file://", "ftp://", "gopher://"
            };
            
            for (String blocked : blockedDomains) {
                if (host.contains(blocked)) {
                    return false;
                }
            }
            
            // Allow common domains (you can expand this list)
            String[] allowedDomains = {
                    "wikipedia.org", "github.com", "stackoverflow.com", "medium.com",
                    "dev.to", "reddit.com", "news.ycombinator.com", "arxiv.org"
            };
            
            for (String allowed : allowedDomains) {
                if (host.endsWith(allowed)) {
                    return true;
                }
            }
            
            // For demo purposes, allow most domains
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private String fetchUrlContent(String url) {
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(java.time.Duration.ofSeconds(toolTimeoutSeconds))
                    .block();
        } catch (Exception e) {
            log.error("Failed to fetch URL content: {}", url, e);
            return null;
        }
    }
    
    private String extractTextFromHtml(String htmlContent) {
        try {
            return tika.parseToString(new ByteArrayInputStream(htmlContent.getBytes()));
        } catch (Exception e) {
            log.error("Failed to extract text from HTML", e);
            return null;
        }
    }
    
    private String generateSummary(String text) {
        if (text.length() <= SUMMARY_LENGTH) {
            return text;
        }
        
        // Simple extractive summarization - take first few sentences
        String[] sentences = text.split("[.!?]+");
        StringBuilder summary = new StringBuilder();
        int currentLength = 0;
        
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty()) continue;
            
            if (currentLength + sentence.length() + 1 <= SUMMARY_LENGTH) {
                if (summary.length() > 0) {
                    summary.append(". ");
                }
                summary.append(sentence);
                currentLength += sentence.length() + 2;
            } else {
                break;
            }
        }
        
        if (summary.length() > 0) {
            summary.append(".");
        }
        
        return summary.toString();
    }
}
