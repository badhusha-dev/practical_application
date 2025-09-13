package com.example.llm.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 255)
    private String filename;
    
    @Column(name = "content_type", length = 100)
    private String contentType;
    
    @Column(nullable = false)
    private Long size;
    
    @Column(nullable = false, length = 64)
    private String checksum;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String tags = "[]";
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    
    public Document(String filename, String contentType, Long size, String checksum) {
        this.filename = filename;
        this.contentType = contentType;
        this.size = size;
        this.checksum = checksum;
    }
}
