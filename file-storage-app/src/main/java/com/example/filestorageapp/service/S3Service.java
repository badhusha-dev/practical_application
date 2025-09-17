package com.example.filestorageapp.service;

import com.example.filestorageapp.config.AwsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AwsConfig awsConfig;
    private S3Client s3Client;
    private S3Presigner s3Presigner;

    private S3Client getS3Client() {
        if (s3Client == null) {
            // Check if AWS credentials are available
            if (awsConfig.getAccessKey() == null || awsConfig.getAccessKey().isEmpty() ||
                awsConfig.getSecretKey() == null || awsConfig.getSecretKey().isEmpty()) {
                log.warn("AWS credentials not configured. Using mock S3 service for development.");
                return null; // Will use mock implementation
            }
            
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                    awsConfig.getAccessKey(), 
                    awsConfig.getSecretKey()
            );
            
            s3Client = S3Client.builder()
                    .region(Region.of(awsConfig.getS3().getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
        }
        return s3Client;
    }

    private S3Presigner getS3Presigner() {
        if (s3Presigner == null) {
            // Check if AWS credentials are available
            if (awsConfig.getAccessKey() == null || awsConfig.getAccessKey().isEmpty() ||
                awsConfig.getSecretKey() == null || awsConfig.getSecretKey().isEmpty()) {
                log.warn("AWS credentials not configured. Using mock S3 presigner for development.");
                return null; // Will use mock implementation
            }
            
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                    awsConfig.getAccessKey(), 
                    awsConfig.getSecretKey()
            );
            
            s3Presigner = S3Presigner.builder()
                    .region(Region.of(awsConfig.getS3().getRegion()))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .build();
        }
        return s3Presigner;
    }

    public void uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        S3Client client = getS3Client();
        if (client == null) {
            log.info("Mock upload: File {} uploaded successfully (development mode)", key);
            return;
        }
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(awsConfig.getS3().getBucketName())
                    .key(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
            log.info("Successfully uploaded file to S3: {}", key);
        } catch (Exception e) {
            log.error("Error uploading file to S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    public void deleteFile(String key) {
        S3Client client = getS3Client();
        if (client == null) {
            log.info("Mock delete: File {} deleted successfully (development mode)", key);
            return;
        }
        
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(awsConfig.getS3().getBucketName())
                    .key(key)
                    .build();

            client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file from S3: {}", key);
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    public String generatePresignedUrl(String key, Duration expiration) {
        S3Presigner presigner = getS3Presigner();
        if (presigner == null) {
            log.info("Mock presigned URL generated for file: {} (development mode)", key);
            return "http://localhost:8080/mock-download/" + key;
        }
        
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(awsConfig.getS3().getBucketName())
                    .key(key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    public boolean fileExists(String key) {
        S3Client client = getS3Client();
        if (client == null) {
            log.info("Mock file exists check for: {} (development mode)", key);
            return true; // Assume file exists in mock mode
        }
        
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(awsConfig.getS3().getBucketName())
                    .key(key)
                    .build();

            client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            log.error("Error checking if file exists: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<String> listFiles(String prefix) {
        S3Client client = getS3Client();
        if (client == null) {
            log.info("Mock list files for prefix: {} (development mode)", prefix);
            return List.of(); // Return empty list in mock mode
        }
        
        try {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(awsConfig.getS3().getBucketName())
                    .prefix(prefix)
                    .build();

            ListObjectsV2Response response = client.listObjectsV2(listObjectsRequest);
            return response.contents().stream()
                    .map(S3Object::key)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error listing files from S3: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to list files from S3", e);
        }
    }

    public void createBucketIfNotExists() {
        S3Client client = getS3Client();
        if (client == null) {
            log.info("Mock bucket creation: {} (development mode)", awsConfig.getS3().getBucketName());
            return;
        }
        
        try {
            String bucketName = awsConfig.getS3().getBucketName();
            
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            try {
                client.headBucket(headBucketRequest);
                log.info("Bucket {} already exists", bucketName);
            } catch (NoSuchBucketException e) {
                CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();

                client.createBucket(createBucketRequest);
                log.info("Created bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Error creating bucket: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create bucket", e);
        }
    }

    public AwsConfig getAwsConfig() {
        return awsConfig;
    }

    public void close() {
        if (s3Client != null) {
            s3Client.close();
        }
        if (s3Presigner != null) {
            s3Presigner.close();
        }
    }
}
