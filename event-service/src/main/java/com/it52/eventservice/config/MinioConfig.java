package com.it52.eventservice.config;

import io.minio.MinioClient;
import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Getter
@Setter
public class MinioConfig {

    private String url;
    private String accessKey;
    private String secretKey;
    private String bucketUser;
    private String bucketEvent;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
