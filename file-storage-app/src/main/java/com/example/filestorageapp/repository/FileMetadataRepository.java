package com.example.filestorageapp.repository;

import com.example.filestorageapp.entity.FileMetadata;
import com.example.filestorageapp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    
    Page<FileMetadata> findByUserAndIsLatestTrue(User user, Pageable pageable);
    
    Page<FileMetadata> findByUserAndIsLatestTrueAndOriginalFilenameContainingIgnoreCase(
            User user, String filename, Pageable pageable);
    
    Optional<FileMetadata> findByUserAndIsLatestTrueAndOriginalFilename(User user, String originalFilename);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.user = :user AND f.isLatest = true AND " +
           "(LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.contentType) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<FileMetadata> findByUserAndSearchTerm(@Param("user") User user, @Param("search") String search, Pageable pageable);
    
    Optional<FileMetadata> findByS3KeyAndS3Bucket(String s3Key, String s3Bucket);
    
    List<FileMetadata> findByUserAndParentFileIsNullAndIsLatestTrue(User user);
    
    List<FileMetadata> findByParentFileAndIsLatestFalse(FileMetadata parentFile);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.user = :user AND f.isLatest = true ORDER BY f.createdAt DESC")
    List<FileMetadata> findLatestFilesByUser(@Param("user") User user, Pageable pageable);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.isLatest = true ORDER BY f.createdAt DESC")
    Page<FileMetadata> findAllLatestFiles(Pageable pageable);
    
    @Query("SELECT f FROM FileMetadata f WHERE f.isLatest = true AND " +
           "(LOWER(f.originalFilename) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.contentType) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<FileMetadata> findAllLatestFilesBySearch(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT SUM(f.fileSize) FROM FileMetadata f WHERE f.user = :user AND f.isLatest = true")
    Long getTotalStorageUsedByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(f) FROM FileMetadata f WHERE f.user = :user AND f.isLatest = true")
    Long getFileCountByUser(@Param("user") User user);
}
