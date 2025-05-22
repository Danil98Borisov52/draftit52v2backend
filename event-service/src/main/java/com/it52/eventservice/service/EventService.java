package com.it52.eventservice.service;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.model.Event;

import java.util.List;

public interface EventService {
    EventCreateDto createEvent(EventCreateDto dto);
    List<Event> getPublicEvents();
    List<Event> getPendingApproval();
    void approveEvent(Long eventId);
    void deleteEvent(Long eventId);
}
