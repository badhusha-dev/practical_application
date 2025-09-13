package com.example.llm.repository;

import com.example.llm.entity.Chunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChunkRepository extends JpaRepository<Chunk, UUID> {
    
    List<Chunk> findByDocumentId(UUID documentId);
    
    @Query(value = """
        SELECT c.id, c.document_id, c.chunk_index, c.text, c.metadata,
               1 - (c.vector <=> :queryVector::vector) AS score
        FROM chunks c
        ORDER BY c.vector <-> :queryVector::vector
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findSimilarChunks(@Param("queryVector") String queryVector, @Param("limit") int limit);
    
    @Query(value = """
        SELECT c.id, c.document_id, c.chunk_index, c.text, c.metadata,
               1 - (c.vector <=> :queryVector::vector) AS score
        FROM chunks c
        WHERE c.document_id = :documentId
        ORDER BY c.vector <-> :queryVector::vector
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findSimilarChunksByDocument(@Param("queryVector") String queryVector, 
                                               @Param("documentId") UUID documentId, 
                                               @Param("limit") int limit);
    
    @Query("SELECT c FROM Chunk c WHERE c.text LIKE %:text%")
    List<Chunk> findByTextContaining(@Param("text") String text);
}
