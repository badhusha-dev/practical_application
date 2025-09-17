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
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private UserRole role = UserRole.USER;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;
    
    @Column(name = "storage_quota")
    @Builder.Default
    private Long storageQuota = 1073741824L; // 1GB default
    
    @Column(name = "used_storage")
    @Builder.Default
    private Long usedStorage = 0L;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<FileMetadata> files = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SharedFile> sharedFiles = new ArrayList<>();
    
    public boolean hasStorageSpace(long fileSize) {
        return (usedStorage + fileSize) <= storageQuota;
    }
    
    public void addUsedStorage(long size) {
        this.usedStorage += size;
    }
    
    public void removeUsedStorage(long size) {
        this.usedStorage = Math.max(0, this.usedStorage - size);
    }
    
    public double getStorageUsagePercentage() {
        if (storageQuota == 0) return 0;
        return (double) usedStorage / storageQuota * 100;
    }
}
