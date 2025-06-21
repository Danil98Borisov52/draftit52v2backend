package com.it52.eventservice.service.api;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.dto.EventResponseDto;

import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    EventResponseDto createEvent(EventCreateDto dto, MultipartFile image);
    void approveEvent(String slug);
    void deleteEvent(String slug);
    EventResponseDto getEvent(String slug);
    EventResponseDto getEventById(Long id);
}
