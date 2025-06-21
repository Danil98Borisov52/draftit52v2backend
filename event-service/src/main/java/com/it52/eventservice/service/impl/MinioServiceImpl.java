package com.it52.eventservice.service.impl;

import com.it52.eventservice.config.MinioConfig;
import com.it52.eventservice.service.api.MinioService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public String uploadFile(MultipartFile file, String eventId) {
        try {
            String fileName = eventId + "_" + file.getOriginalFilename();

            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucket())
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return minioConfig.getUrl() + "/" + minioConfig.getBucket() + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
    }
}
