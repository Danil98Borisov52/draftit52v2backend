package com.it52.eventservice.service.api;

import com.it52.eventservice.dto.event.EventRequestDTO;
import com.it52.eventservice.dto.event.EventResponseDTO;

import com.it52.eventservice.dto.event.EventUpdateRequestDTO;
import org.springframework.web.multipart.MultipartFile;

public interface EventService {
    EventResponseDTO createEvent(EventRequestDTO dto, MultipartFile image);
    void approveEvent(String slug);
    void deleteEvent(String slug);
    EventResponseDTO getEvent(String slug);
    EventResponseDTO getEventById(Long id);
    EventResponseDTO updateEvent(String slug, EventUpdateRequestDTO dto, MultipartFile image);
}
