package com.it52.eventservice.controller;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.dto.EventResponseDto;
import com.it52.eventservice.mapper.EventMapper;
import com.it52.eventservice.model.Event;
import com.it52.eventservice.service.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.isNumeric;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

/*    @PostMapping("/new")
    public ResponseEntity<EventResponseDto> create(@RequestBody EventCreateDto eventDto) {
        EventResponseDto event = eventService.createEvent(eventDto);
        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }*/

    @PostMapping(value = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventResponseDto> create(
            @RequestPart("event") EventCreateDto eventDto,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        EventResponseDto createdEvent = eventService.createEvent(eventDto, image);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping("/public")
    public Page<EventResponseDto> getPublicEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String kind,
            @RequestParam(defaultValue = "future") String status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return eventService.getPublicEvents(pageable, kind, status);
    }

    @GetMapping("/{param}")
    public ResponseEntity<EventResponseDto> getEventBySlug(@PathVariable String param) {
        if (isNumeric(param)) {
            return ResponseEntity.ok(eventService.getEventById(Long.parseLong(param)));
        } else {
            return ResponseEntity.ok(eventService.getEvent(param));
        }
    }

    @GetMapping("/moderation")
    public Page<EventResponseDto> getPending(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String kind,
            @RequestParam(defaultValue = "future") String status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return eventService.getPendingApproval(pageable, kind, status);
    }

    @PutMapping("/{slug}/approve")
    public void approve(@PathVariable String slug) {
        eventService.approveEvent(slug);
    }

    @DeleteMapping("/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable String slug) {
        eventService.deleteEvent(slug);
        return ResponseEntity.noContent().build();
    }
}

