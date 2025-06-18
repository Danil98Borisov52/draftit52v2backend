package com.it52.eventservice.service;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.dto.EventResponseDto;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface EventService {
    EventResponseDto createEvent(EventCreateDto dto);
    Page<EventResponseDto> getPublicEvents(Pageable pageable, String kind, String status);
    Page<EventResponseDto> getPendingApproval(Pageable pageable, String kind, String status);
    void approveEvent(String slug);
    void deleteEvent(String slug);
    EventResponseDto getEvent(String slug);
    EventResponseDto getEventById(Long id);
    void listenUserRegisteredToEvent(String message);
}
