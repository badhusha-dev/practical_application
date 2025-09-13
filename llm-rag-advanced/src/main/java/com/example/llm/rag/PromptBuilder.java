package com.example.llm.rag;

import com.example.llm.dto.ChatMessageDTO;
import com.example.llm.llm.ChatMessage;
import com.example.llm.llm.ChatPrompt;
import com.example.llm.llm.ToolDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromptBuilder {
    
    @Value("${app.rag.systemPrompt}")
    private String systemPrompt;
    
    @Value("${app.openai.temperature:0.7}")
    private Double defaultTemperature;
    
    @Value("${app.openai.maxTokens:2000}")
    private Integer defaultMaxTokens;
    
    public ChatPrompt buildPrompt(String userMessage, List<RetrieverService.ContextSnippet> contextSnippets, 
                                 List<ChatMessageDTO> history, List<ToolDefinition> tools) {
        log.debug("Building prompt with {} context snippets and {} history messages", 
                 contextSnippets.size(), history.size());
        
        String context = buildContext(contextSnippets);
        List<ChatMessage> chatHistory = convertHistory(history);
        
        return new ChatPrompt(
                systemPrompt,
                userMessage,
                context,
                chatHistory,
                tools,
                defaultTemperature,
                defaultMaxTokens
        );
    }
    
    public ChatPrompt buildPrompt(String userMessage, List<RetrieverService.ContextSnippet> contextSnippets) {
        return buildPrompt(userMessage, contextSnippets, new ArrayList<>(), new ArrayList<>());
    }
    
    public ChatPrompt buildPrompt(String userMessage, List<ChatMessageDTO> history, List<ToolDefinition> tools) {
        return buildPrompt(userMessage, new ArrayList<>(), history, tools);
    }
    
    public ChatPrompt buildPrompt(String userMessage) {
        return buildPrompt(userMessage, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
    
    private String buildContext(List<RetrieverService.ContextSnippet> contextSnippets) {
        if (contextSnippets.isEmpty()) {
            return "";
        }
        
        StringBuilder context = new StringBuilder();
        context.append("Relevant information from the knowledge base:\n\n");
        
        for (RetrieverService.ContextSnippet snippet : contextSnippets) {
            context.append(snippet.getSourceReference())
                   .append(" ")
                   .append(snippet.getText())
                   .append("\n\n");
        }
        
        return context.toString();
    }
    
    private List<ChatMessage> convertHistory(List<ChatMessageDTO> history) {
        List<ChatMessage> chatHistory = new ArrayList<>();
        
        for (ChatMessageDTO message : history) {
            ChatMessage chatMessage = new ChatMessage(
                    message.getRole(),
                    message.getContent(),
                    null // name field for tool calls
            );
            chatHistory.add(chatMessage);
        }
        
        return chatHistory;
    }
    
    public String buildSystemPrompt() {
        return systemPrompt;
    }
    
    public String buildUserPrompt(String userMessage) {
        return userMessage;
    }
    
    public String buildContextPrompt(List<RetrieverService.ContextSnippet> contextSnippets) {
        return buildContext(contextSnippets);
    }
    
    public List<ChatMessage> assemblePrompt(String systemPrompt, String userMessage, 
                                          String context, List<ChatMessageDTO> history) {
        List<ChatMessage> messages = new ArrayList<>();
        
        // Add system message with context
        if (context != null && !context.isEmpty()) {
            String fullSystemPrompt = systemPrompt + "\n\n" + context;
            messages.add(new ChatMessage("system", fullSystemPrompt, null));
        } else {
            messages.add(new ChatMessage("system", systemPrompt, null));
        }
        
        // Add history
        List<ChatMessage> chatHistory = convertHistory(history);
        messages.addAll(chatHistory);
        
        // Add user message
        messages.add(new ChatMessage("user", userMessage, null));
        
        return messages;
    }
}
