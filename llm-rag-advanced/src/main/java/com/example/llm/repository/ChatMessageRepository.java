package com.example.llm.repository;

import com.example.llm.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {
    
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(UUID sessionId);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sessionId = :sessionId AND cm.role = :role ORDER BY cm.createdAt ASC")
    List<ChatMessage> findBySessionIdAndRoleOrderByCreatedAtAsc(@Param("sessionId") UUID sessionId, @Param("role") String role);
    
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.sessionId = :sessionId ORDER BY cm.createdAt DESC LIMIT :limit")
    List<ChatMessage> findRecentMessagesBySessionId(@Param("sessionId") UUID sessionId, @Param("limit") int limit);
}
