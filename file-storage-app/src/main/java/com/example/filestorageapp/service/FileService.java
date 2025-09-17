package com.example.filestorageapp.service;

import com.example.filestorageapp.entity.FileMetadata;
import com.example.filestorageapp.entity.User;
import com.example.filestorageapp.repository.FileMetadataRepository;
import com.example.filestorageapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public FileMetadata uploadFile(MultipartFile file, User user) throws IOException {
        // Check storage quota
        if (!user.hasStorageSpace(file.getSize())) {
            throw new RuntimeException("Insufficient storage space");
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = generateUniqueFilename(originalFilename);
        String s3Key = "users/" + user.getId() + "/" + uniqueFilename;

        // Calculate file hash
        String fileHash = calculateFileHash(file.getBytes());

        // Check if file already exists (for versioning)
        Optional<FileMetadata> existingFile = fileMetadataRepository
                .findByUserAndIsLatestTrueAndOriginalFilename(user, originalFilename);

        FileMetadata fileMetadata;
        if (existingFile.isPresent()) {
            // Create new version
            FileMetadata parentFile = existingFile.get();
            parentFile.setIsLatest(false);
            fileMetadataRepository.save(parentFile);

            fileMetadata = FileMetadata.builder()
                    .filename(uniqueFilename)
                    .originalFilename(originalFilename)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .s3Key(s3Key)
                    .s3Bucket(s3Service.getAwsConfig().getS3().getBucketName())
                    .fileHash(fileHash)
                    .version(parentFile.getVersion() + 1)
                    .isLatest(true)
                    .parentFile(parentFile)
                    .user(user)
                    .build();
        } else {
            // Create new file
            fileMetadata = FileMetadata.builder()
                    .filename(uniqueFilename)
                    .originalFilename(originalFilename)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .s3Key(s3Key)
                    .s3Bucket(s3Service.getAwsConfig().getS3().getBucketName())
                    .fileHash(fileHash)
                    .version(1)
                    .isLatest(true)
                    .user(user)
                    .build();
        }

        // Upload to S3
        s3Service.uploadFile(s3Key, file.getInputStream(), file.getSize(), file.getContentType());

        // Save metadata
        fileMetadata = fileMetadataRepository.save(fileMetadata);

        // Update user storage usage
        user.addUsedStorage(file.getSize());
        userRepository.save(user);

        log.info("File uploaded successfully: {} by user: {}", originalFilename, user.getUsername());
        return fileMetadata;
    }

    @Transactional
    public void deleteFile(Long fileId, User user) {
        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!fileMetadata.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Access denied");
        }

        // Delete from S3
        s3Service.deleteFile(fileMetadata.getS3Key());

        // Update user storage usage
        user.removeUsedStorage(fileMetadata.getFileSize());
        userRepository.save(user);

        // Delete metadata
        fileMetadataRepository.delete(fileMetadata);

        log.info("File deleted successfully: {} by user: {}", fileMetadata.getOriginalFilename(), user.getUsername());
    }

    public Page<FileMetadata> getUserFiles(User user, Pageable pageable) {
        return fileMetadataRepository.findByUserAndIsLatestTrue(user, pageable);
    }

    public Page<FileMetadata> searchUserFiles(User user, String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getUserFiles(user, pageable);
        }
        return fileMetadataRepository.findByUserAndSearchTerm(user, searchTerm.trim(), pageable);
    }

    public Optional<FileMetadata> getFileById(Long fileId) {
        return fileMetadataRepository.findById(fileId);
    }

    public String generateDownloadUrl(Long fileId, User user) {
        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!fileMetadata.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Access denied");
        }

        return s3Service.generatePresignedUrl(fileMetadata.getS3Key(), Duration.ofHours(1));
    }

    public List<FileMetadata> getFileVersions(Long fileId, User user) {
        FileMetadata fileMetadata = fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!fileMetadata.getUser().getId().equals(user.getId()) && !user.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Access denied");
        }

        FileMetadata parentFile = fileMetadata.getParentFile() != null ? 
                fileMetadata.getParentFile() : fileMetadata;

        return fileMetadataRepository.findByParentFileAndIsLatestFalse(parentFile);
    }

    private String generateUniqueFilename(String originalFilename) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String extension = getFileExtension(originalFilename);
        return timestamp + "_" + uuid + (extension.isEmpty() ? "" : "." + extension);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1).toLowerCase() : "";
    }

    private String calculateFileHash(byte[] fileBytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("Error calculating file hash", e);
            return "";
        }
    }

    // Admin methods
    public Page<FileMetadata> getAllLatestFiles(Pageable pageable) {
        return fileMetadataRepository.findAllLatestFiles(pageable);
    }

    public Page<FileMetadata> getAllLatestFilesBySearch(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllLatestFiles(pageable);
        }
        return fileMetadataRepository.findAllLatestFilesBySearch(searchTerm.trim(), pageable);
    }
}
