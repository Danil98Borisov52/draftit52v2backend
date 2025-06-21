package com.it52.eventservice.service.impl;

import com.it52.eventservice.model.Event;
import com.it52.eventservice.repository.EventRepository;
import com.it52.eventservice.service.api.EventImageService;
import com.it52.eventservice.service.api.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class EventImageServiceImpl implements EventImageService {

    private final EventRepository eventRepository;
    private final MinioService minioService;


    @Override
    public void uploadEventImageIfPresent(MultipartFile image, Event event) {
        if (image != null && !image.isEmpty()) {
            String imageUrl = minioService.uploadFile(image, event.getId().toString());
            event.setTitleImage(imageUrl);
            eventRepository.save(event);
        }
    }
}