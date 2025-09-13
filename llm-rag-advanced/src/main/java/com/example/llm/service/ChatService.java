package com.example.llm.service;

import com.example.llm.dto.ChatMessageDTO;
import com.example.llm.dto.ChatRequest;
import com.example.llm.dto.ChatResponse;
import com.example.llm.dto.ChatSessionDTO;
import com.example.llm.entity.ChatMessage;
import com.example.llm.entity.ChatSession;
import com.example.llm.entity.User;
import com.example.llm.llm.*;
import com.example.llm.rag.PromptBuilder;
import com.example.llm.rag.RetrieverService;
import com.example.llm.repository.ChatMessageRepository;
import com.example.llm.repository.ChatSessionRepository;
import com.example.llm.tools.ToolRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    
    private final LlmProvider llmProvider;
    private final RetrieverService retrieverService;
    private final PromptBuilder promptBuilder;
    private final ToolRegistry toolRegistry;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    
    @Value("${app.tools.maxToolCalls:3}")
    private int maxToolCalls;
    
    public Flux<ChatResponse> chatStream(ChatRequest request, User user) {
        log.debug("Starting chat stream for user: {} with message: {}", user.getUsername(), request.getMessage());
        
        return Flux.create(sink -> {
            try {
                UUID sessionId = request.getSessionId() != null ? request.getSessionId() : createNewSession(user);
                
                // Retrieve context if RAG is enabled
                List<RetrieverService.ContextSnippet> contextSnippets = new ArrayList<>();
                if (request.isUseRag()) {
                    contextSnippets = retrieverService.retrieveContext(request.getMessage(), request.getTopK());
                }
                
                // Get chat history
                List<ChatMessageDTO> history = getRecentHistory(sessionId, 10);
                
                // Build tools list
                List<ToolDefinition> tools = buildToolsList(request.getTools());
                
                // Build prompt
                ChatPrompt prompt = promptBuilder.buildPrompt(
                        request.getMessage(),
                        contextSnippets,
                        history,
                        tools
                );
                
                // Save user message
                saveMessage(sessionId, "user", request.getMessage(), 0, 0);
                
                // Stream response
                streamResponse(prompt, sessionId, sink, 0);
                
            } catch (Exception e) {
                log.error("Error in chat stream", e);
                sink.next(new ChatResponse("error", null, null, null, null, e.getMessage()));
                sink.complete();
            }
        });
    }
    
    public ChatResponse chatNonStream(ChatRequest request, User user) {
        log.debug("Starting non-streaming chat for user: {} with message: {}", user.getUsername(), request.getMessage());
        
        try {
            UUID sessionId = request.getSessionId() != null ? request.getSessionId() : createNewSession(user);
            
            // Retrieve context if RAG is enabled
            List<RetrieverService.ContextSnippet> contextSnippets = new ArrayList<>();
            if (request.isUseRag()) {
                contextSnippets = retrieverService.retrieveContext(request.getMessage(), request.getTopK());
            }
            
            // Get chat history
            List<ChatMessageDTO> history = getRecentHistory(sessionId, 10);
            
            // Build tools list
            List<ToolDefinition> tools = buildToolsList(request.getTools());
            
            // Build prompt
            ChatPrompt prompt = promptBuilder.buildPrompt(
                    request.getMessage(),
                    contextSnippets,
                    history,
                    tools
            );
            
            // Save user message
            saveMessage(sessionId, "user", request.getMessage(), 0, 0);
            
            // Get complete response
            String response = llmProvider.chat(prompt).block();
            
            // Handle tool calls if any
            response = handleToolCalls(response, sessionId, prompt);
            
            // Save assistant message
            saveMessage(sessionId, "assistant", response, estimateTokens(prompt), estimateTokens(response));
            
            return new ChatResponse("done", null, response, null, null, null);
            
        } catch (Exception e) {
            log.error("Error in non-streaming chat", e);
            return new ChatResponse("error", null, null, null, null, e.getMessage());
        }
    }
    
    private void streamResponse(ChatPrompt prompt, UUID sessionId, reactor.core.publisher.FluxSink<ChatResponse> sink, int toolCallCount) {
        llmProvider.chatStream(prompt)
                .flatMapMany(Flux::fromIterable)
                .doOnNext(token -> sink.next(new ChatResponse("delta", token, null, null, null, null)))
                .collectList()
                .map(tokens -> String.join("", tokens))
                .doOnNext(response -> {
                    // Handle tool calls
                    String finalResponse = handleToolCalls(response, sessionId, prompt, sink, toolCallCount);
                    
                    // Save assistant message
                    saveMessage(sessionId, "assistant", finalResponse, estimateTokens(prompt), estimateTokens(finalResponse));
                    
                    sink.next(new ChatResponse("done", null, finalResponse, null, null, null));
                    sink.complete();
                })
                .doOnError(error -> {
                    log.error("Error in stream response", error);
                    sink.next(new ChatResponse("error", null, null, null, null, error.getMessage()));
                    sink.complete();
                })
                .subscribe();
    }
    
    private String handleToolCalls(String response, UUID sessionId, ChatPrompt prompt) {
        return handleToolCalls(response, sessionId, prompt, null, 0);
    }
    
    private String handleToolCalls(String response, UUID sessionId, ChatPrompt prompt, 
                                 reactor.core.publisher.FluxSink<ChatResponse> sink, int toolCallCount) {
        List<ToolCallParser.ToolCall> toolCalls = new ToolCallParser().parseToolCalls(response);
        
        if (toolCalls.isEmpty() || toolCallCount >= maxToolCalls) {
            return response;
        }
        
        StringBuilder finalResponse = new StringBuilder(response);
        
        for (ToolCallParser.ToolCall toolCall : toolCalls) {
            if (sink != null) {
                sink.next(new ChatResponse("tool_call", null, null, toolCall.getName(), toolCall.getArgs(), null));
            }
            
            CompletableFuture<String> toolResult = toolRegistry.invokeTool(toolCall.getName(), toolCall.getArgs());
            String result = toolResult.join();
            
            if (sink != null) {
                sink.next(new ChatResponse("tool_result", null, result, toolCall.getName(), null, null));
            }
            
            // Save tool call and result
            saveMessage(sessionId, "tool", toolCall.getName() + ": " + toolCall.getArgs(), 0, 0);
            saveMessage(sessionId, "tool", result, 0, 0);
            
            // Continue generation with tool result
            String continuationPrompt = response + "\n\nTool result: " + result + "\n\nPlease continue your response:";
            ChatPrompt continuationPromptObj = new ChatPrompt(
                    prompt.getSystemPrompt(),
                    continuationPrompt,
                    prompt.getContext(),
                    prompt.getHistory(),
                    prompt.getTools(),
                    prompt.getTemperature(),
                    prompt.getMaxTokens()
            );
            
            String continuation = llmProvider.chat(continuationPromptObj).block();
            finalResponse.append("\n\n").append(continuation);
            
            // Recursively handle more tool calls
            if (toolCallCount + 1 < maxToolCalls) {
                continuation = handleToolCalls(continuation, sessionId, continuationPromptObj, sink, toolCallCount + 1);
                finalResponse.append(continuation);
            }
        }
        
        return finalResponse.toString();
    }
    
    private UUID createNewSession(User user) {
        ChatSession session = new ChatSession(user.getId(), "New Chat");
        ChatSession savedSession = chatSessionRepository.save(session);
        log.debug("Created new chat session: {} for user: {}", savedSession.getId(), user.getUsername());
        return savedSession.getId();
    }
    
    private List<ChatMessageDTO> getRecentHistory(UUID sessionId, int limit) {
        List<ChatMessage> messages = chatMessageRepository.findRecentMessagesBySessionId(sessionId, limit);
        return messages.stream()
                .map(msg -> new ChatMessageDTO(
                        msg.getId(),
                        msg.getSessionId(),
                        msg.getRole(),
                        msg.getContent(),
                        msg.getTokensIn(),
                        msg.getTokensOut(),
                        msg.getCreatedAt()
                ))
                .collect(java.util.stream.Collectors.toList());
    }
    
    private List<ToolDefinition> buildToolsList(List<String> toolNames) {
        if (toolNames == null || toolNames.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ToolDefinition> tools = new ArrayList<>();
        for (String toolName : toolNames) {
            ToolRegistry.Tool tool = toolRegistry.getTool(toolName);
            if (tool != null) {
                tools.add(new ToolDefinition(tool.getName(), tool.getDescription(), tool.getParameters()));
            }
        }
        
        return tools;
    }
    
    private void saveMessage(UUID sessionId, String role, String content, int tokensIn, int tokensOut) {
        ChatMessage message = new ChatMessage(sessionId, role, content, tokensIn, tokensOut);
        chatMessageRepository.save(message);
    }
    
    private int estimateTokens(String text) {
        // Simple token estimation: ~4 characters per token
        return text != null ? text.length() / 4 : 0;
    }
    
    public List<ChatSessionDTO> getUserSessions(User user) {
        List<ChatSession> sessions = chatSessionRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return sessions.stream()
                .map(session -> new ChatSessionDTO(
                        session.getId(),
                        session.getUserId(),
                        session.getTitle(),
                        session.getCreatedAt()
                ))
                .collect(java.util.stream.Collectors.toList());
    }
}
