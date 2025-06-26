package com.it52.eventservice.service.api;

import com.it52.eventservice.dto.EventDto;
import com.it52.eventservice.dto.EventResponseDto;

import com.it52.eventservice.dto.EventUpdateDto;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    EventResponseDto createEvent(EventDto dto, MultipartFile image);
    void approveEvent(String slug);
    void deleteEvent(String slug);
    EventResponseDto getEvent(String slug);
    EventResponseDto getEventById(Long id);
    EventResponseDto updateEvent(String slug, EventUpdateDto dto, MultipartFile image);
}
