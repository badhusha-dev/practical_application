package com.example.llm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "chunks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chunk {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "document_id", nullable = false)
    private UUID documentId;
    
    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;
    
    @Column(columnDefinition = "vector(1536)")
    private String vector;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String metadata = "{}";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", insertable = false, updatable = false)
    private Document document;
    
    public Chunk(UUID documentId, Integer chunkIndex, String text) {
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.text = text;
    }
}
