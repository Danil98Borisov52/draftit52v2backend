package com.it52.eventservice.service;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.dto.EventResponseDto;
import com.it52.eventservice.model.Event;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface EventService {
    EventResponseDto createEvent(EventCreateDto dto);
    Page<EventResponseDto> getPublicEvents(Pageable pageable, String kind, String status);
    List<Event> getPendingApproval();
    void approveEvent(String slug);
    void deleteEvent(String slug);
    EventResponseDto getEvent(String slug);
    EventResponseDto getEventById(Long id);
    void listenUserRegisteredToEvent(String message);
}
