package com.example.filestorageapp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsConfig {
    
    private S3 s3 = new S3();
    private String accessKey;
    private String secretKey;
    
    @Data
    public static class S3 {
        private String bucketName;
        private String region = "us-east-1";
    }
}
