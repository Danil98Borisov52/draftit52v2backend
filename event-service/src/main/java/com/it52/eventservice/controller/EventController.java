package com.it52.eventservice.controller;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.model.Event;
import com.it52.eventservice.service.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/new")
    public ResponseEntity<EventCreateDto> create(@RequestBody EventCreateDto eventDto) {
        EventCreateDto event = eventService.createEvent(eventDto);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    @GetMapping("/public")
    public List<Event> getPublicEvents() {
        return eventService.getPublicEvents();
    }

    @GetMapping("/moderation")
    public List<Event> getPending() {
        return eventService.getPendingApproval();
    }

    @PutMapping("/{id}/approve")
    public void approve(@PathVariable Long id) {
        eventService.approveEvent(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}

