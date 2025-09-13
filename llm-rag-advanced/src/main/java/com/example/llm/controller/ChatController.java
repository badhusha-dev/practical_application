package com.example.llm.controller;

import com.example.llm.dto.ChatRequest;
import com.example.llm.dto.ChatResponse;
import com.example.llm.entity.User;
import com.example.llm.service.ChatService;
import com.example.llm.service.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    
    private final ChatService chatService;
    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;
    
    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestBody ChatRequest request, 
                                  @AuthenticationPrincipal User user) {
        log.info("Chat stream request from user: {}", user.getUsername());
        
        // Check rate limit
        if (!rateLimitService.isAllowed(user.getId())) {
            return Flux.just("data: " + createErrorResponse("Rate limit exceeded").toJson() + "\n\n");
        }
        
        return chatService.chatStream(request, user)
                .map(response -> "data: " + response.toJson() + "\n\n")
                .doOnError(error -> log.error("Error in chat stream", error))
                .onErrorReturn("data: " + createErrorResponse(error.getMessage()).toJson() + "\n\n");
    }
    
    @PostMapping("/chat/nonstream")
    public ResponseEntity<ChatResponse> chatNonStream(@RequestBody ChatRequest request,
                                                     @AuthenticationPrincipal User user) {
        log.info("Non-streaming chat request from user: {}", user.getUsername());
        
        // Check rate limit
        if (!rateLimitService.isAllowed(user.getId())) {
            return ResponseEntity.status(429).body(createErrorResponse("Rate limit exceeded"));
        }
        
        try {
            ChatResponse response = chatService.chatNonStream(request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error in non-streaming chat", e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/chat/sessions")
    public ResponseEntity<?> getUserSessions(@AuthenticationPrincipal User user) {
        log.info("Get sessions request from user: {}", user.getUsername());
        
        try {
            return ResponseEntity.ok(chatService.getUserSessions(user));
        } catch (Exception e) {
            log.error("Error getting user sessions", e);
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }
    
    private ChatResponse createErrorResponse(String error) {
        return new ChatResponse("error", null, null, null, null, error);
    }
}
