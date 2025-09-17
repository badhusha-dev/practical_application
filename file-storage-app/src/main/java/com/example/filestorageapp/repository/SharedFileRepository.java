package com.example.filestorageapp.repository;

import com.example.filestorageapp.entity.SharedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SharedFileRepository extends JpaRepository<SharedFile, Long> {
    
    Optional<SharedFile> findByShareToken(String shareToken);
    
    @Query("SELECT sf FROM SharedFile sf WHERE sf.shareToken = :token AND sf.isActive = true " +
           "AND (sf.expiresAt IS NULL OR sf.expiresAt > :now)")
    Optional<SharedFile> findActiveByShareToken(@Param("token") String shareToken, @Param("now") LocalDateTime now);
    
    List<SharedFile> findByFileMetadataIdAndIsActiveTrue(Long fileId);
    
    List<SharedFile> findByUserIdAndIsActiveTrue(Long userId);
    
    @Query("SELECT sf FROM SharedFile sf WHERE sf.expiresAt < :now AND sf.isActive = true")
    List<SharedFile> findExpiredShares(@Param("now") LocalDateTime now);
    
    @Query("SELECT sf FROM SharedFile sf WHERE sf.isActive = true ORDER BY sf.createdAt DESC")
    List<SharedFile> findAllActiveShares();
}
