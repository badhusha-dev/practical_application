package com.example.filestorageapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "file_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String filename;
    
    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;
    
    @Column(name = "file_size", nullable = false)
    private Long fileSize;
    
    @Column(name = "content_type", length = 100)
    private String contentType;
    
    @Column(name = "s3_key", nullable = false, length = 500)
    private String s3Key;
    
    @Column(name = "s3_bucket", nullable = false, length = 100)
    private String s3Bucket;
    
    @Column(name = "file_hash", length = 64)
    private String fileHash;
    
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;
    
    @Column(name = "is_latest", nullable = false)
    @Builder.Default
    private Boolean isLatest = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "fileMetadata", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SharedFile> sharedFiles = new ArrayList<>();
    
    @OneToMany(mappedBy = "parentFile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FileMetadata> versions = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_file_id")
    private FileMetadata parentFile;
    
    public String getFileExtension() {
        if (originalFilename == null) return "";
        int lastDotIndex = originalFilename.lastIndexOf('.');
        return lastDotIndex > 0 ? originalFilename.substring(lastDotIndex + 1).toLowerCase() : "";
    }
    
    public String getDisplayName() {
        return originalFilename != null ? originalFilename : filename;
    }
    
    public String getFormattedFileSize() {
        if (fileSize == null) return "0 B";
        
        String[] units = {"B", "KB", "MB", "GB", "TB"};
        int unitIndex = 0;
        double size = fileSize.doubleValue();
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
    
    public Long getParentFileId() {
        return parentFile != null ? parentFile.getId() : null;
    }
}
