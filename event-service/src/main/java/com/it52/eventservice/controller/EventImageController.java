package com.it52.eventservice.controller;

import com.it52.eventservice.model.Event;
import com.it52.eventservice.repository.EventRepository;
import com.it52.eventservice.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventImageController {

    private final MinioService minioService;
    private final EventRepository eventRepository;

    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam MultipartFile image) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        String imageUrl = minioService.uploadFile(image, String.valueOf(id));
        event.setTitleImage(imageUrl);
        eventRepository.save(event);

        return ResponseEntity.ok().body(Map.of("imageUrl", imageUrl));
    }
}
