package com.example.llm.repository;

import com.example.llm.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
    Optional<Document> findByChecksum(String checksum);
    
    @Query("SELECT d FROM Document d WHERE d.filename LIKE %:filename%")
    List<Document> findByFilenameContaining(@Param("filename") String filename);
    
    @Query("SELECT d FROM Document d WHERE JSON_EXTRACT(d.tags, '$') LIKE %:tag%")
    List<Document> findByTagContaining(@Param("tag") String tag);
}
