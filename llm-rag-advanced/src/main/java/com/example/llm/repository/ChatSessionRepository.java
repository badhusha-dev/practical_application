package com.example.llm.repository;

import com.example.llm.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, UUID> {
    
    List<ChatSession> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    @Query("SELECT cs FROM ChatSession cs WHERE cs.userId = :userId AND cs.title LIKE %:title%")
    List<ChatSession> findByUserIdAndTitleContaining(@Param("userId") UUID userId, @Param("title") String title);
}
