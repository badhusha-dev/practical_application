package com.example.filestorageapp.service;

import com.example.filestorageapp.entity.FileMetadata;
import com.example.filestorageapp.entity.SharedFile;
import com.example.filestorageapp.entity.User;
import com.example.filestorageapp.repository.FileMetadataRepository;
import com.example.filestorageapp.repository.SharedFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileSharingService {

    private final SharedFileRepository sharedFileRepository;
    private final FileMetadataRepository fileMetadataRepository;
    private final S3Service s3Service;

    @Transactional
    public SharedFile createShare(Long fileId, User user, LocalDateTime expiresAt, Integer maxDownloads) {
        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!fileMetadata.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        String shareToken = generateShareToken();

        SharedFile sharedFile = SharedFile.builder()
                .shareToken(shareToken)
                .expiresAt(expiresAt)
                .maxDownloads(maxDownloads)
                .isActive(true)
                .fileMetadata(fileMetadata)
                .user(user)
                .build();

        sharedFile = sharedFileRepository.save(sharedFile);
        log.info("File share created: {} for file: {}", shareToken, fileMetadata.getOriginalFilename());
        return sharedFile;
    }

    public Optional<SharedFile> getSharedFile(String shareToken) {
        return sharedFileRepository.findActiveByShareToken(shareToken, LocalDateTime.now());
    }

    @Transactional
    public String getSharedFileDownloadUrl(String shareToken) {
        SharedFile sharedFile = sharedFileRepository.findActiveByShareToken(shareToken, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Share not found or expired"));

        if (!sharedFile.canDownload()) {
            throw new RuntimeException("Share limit exceeded or expired");
        }

        // Increment download count
        sharedFile.incrementDownloadCount();
        sharedFileRepository.save(sharedFile);

        return s3Service.generatePresignedUrl(sharedFile.getFileMetadata().getS3Key(), Duration.ofHours(1));
    }

    @Transactional
    public void revokeShare(Long shareId, User user) {
        SharedFile sharedFile = sharedFileRepository.findById(shareId)
                .orElseThrow(() -> new RuntimeException("Share not found"));

        if (!sharedFile.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Access denied");
        }

        sharedFile.setIsActive(false);
        sharedFileRepository.save(sharedFile);
        log.info("File share revoked: {} by user: {}", sharedFile.getShareToken(), user.getUsername());
    }

    public List<SharedFile> getUserShares(User user) {
        return sharedFileRepository.findByUserIdAndIsActiveTrue(user.getId());
    }

    public List<SharedFile> getAllActiveShares() {
        return sharedFileRepository.findAllActiveShares();
    }

    @Transactional
    public void cleanupExpiredShares() {
        List<SharedFile> expiredShares = sharedFileRepository.findExpiredShares(LocalDateTime.now());
        for (SharedFile share : expiredShares) {
            share.setIsActive(false);
        }
        sharedFileRepository.saveAll(expiredShares);
        log.info("Cleaned up {} expired shares", expiredShares.size());
    }

    private String generateShareToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
