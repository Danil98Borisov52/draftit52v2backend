package com.it52.eventservice.controller;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.dto.EventResponseDto;
import com.it52.eventservice.service.api.EventQueryService;
import com.it52.eventservice.service.api.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import static org.apache.commons.lang.StringUtils.isNumeric;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventQueryService eventQueryService;

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
        return eventQueryService.getPublicEvents(pageable, kind, status);
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
        return eventQueryService.getPendingApproval(pageable, kind, status);
    }

    @GetMapping("/{slug}/approve")
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

