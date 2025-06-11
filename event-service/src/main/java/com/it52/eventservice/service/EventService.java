package com.it52.eventservice.service;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.dto.EventResponseDto;
import com.it52.eventservice.model.Event;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface EventService {
    EventResponseDto createEvent(EventCreateDto dto);
    Page<EventResponseDto> getPublicEvents(Pageable pageable, String kind, String status);
    List<Event> getPendingApproval();
    void approveEvent(Long eventId);
    void deleteEvent(Long eventId);
    EventResponseDto getEvent(String slug);
    EventResponseDto getEventById(Long id);
}
