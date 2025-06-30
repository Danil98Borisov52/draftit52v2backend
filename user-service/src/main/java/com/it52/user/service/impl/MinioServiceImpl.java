package com.it52.user.service.impl;


import com.it52.user.config.MinioConfig;
import com.it52.user.service.api.MinioService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public String uploadFile(MultipartFile file, String eventId) {
        try {
            String extension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf('.'));
            String fileName = UUID.randomUUID() + extension;

            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
