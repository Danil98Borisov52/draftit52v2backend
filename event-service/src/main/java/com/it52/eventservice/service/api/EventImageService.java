package com.it52.eventservice.service.api;

import com.it52.eventservice.model.Event;
import org.springframework.web.multipart.MultipartFile;

public interface EventImageService {
    void uploadEventImageIfPresent(MultipartFile image, Event event);
}