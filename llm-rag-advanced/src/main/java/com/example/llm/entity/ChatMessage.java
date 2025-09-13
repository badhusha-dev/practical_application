package com.example.llm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "session_id", nullable = false)
    private UUID sessionId;
    
    @Column(nullable = false, length = 20)
    private String role; // system, user, assistant, tool
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "tokens_in")
    private Integer tokensIn = 0;
    
    @Column(name = "tokens_out")
    private Integer tokensOut = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", insertable = false, updatable = false)
    private ChatSession session;
    
    public ChatMessage(UUID sessionId, String role, String content) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
    }
    
    public ChatMessage(UUID sessionId, String role, String content, Integer tokensIn, Integer tokensOut) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.tokensIn = tokensIn;
        this.tokensOut = tokensOut;
    }
}
