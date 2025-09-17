package com.example.filestorageapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shared_files")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedFile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "share_token", unique = true, nullable = false, length = 64)
    private String shareToken;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "download_count")
    @Builder.Default
    private Integer downloadCount = 0;
    
    @Column(name = "max_downloads")
    private Integer maxDownloads;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private FileMetadata fileMetadata;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean canDownload() {
        return isActive && !isExpired() && 
               (maxDownloads == null || downloadCount < maxDownloads);
    }
    
    public void incrementDownloadCount() {
        this.downloadCount++;
    }
}
