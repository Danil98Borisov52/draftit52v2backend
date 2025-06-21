package com.it52.eventservice.service.impl;

import com.it52.eventservice.dto.EventResponseDto;
import com.it52.eventservice.enums.EventKind;
import com.it52.eventservice.enums.EventStatus;
import com.it52.eventservice.model.Event;
import com.it52.eventservice.repository.EventRepository;
import com.it52.eventservice.service.api.AuthorService;
import com.it52.eventservice.service.api.EventQueryService;
import com.it52.eventservice.service.api.ParticipantService;
import com.it52.eventservice.service.api.TaggingService;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;

import static com.it52.eventservice.mapper.EventMapper.toDto;

@RequiredArgsConstructor
@Service
public class EventQueryServiceImpl implements EventQueryService {

    private final EventRepository eventRepository;
    private final TaggingService taggingService;
    private final AuthorService authorService;
    private final ParticipantService participantService;
    private final MinioClient minioClient;

    @Override
    public Page<EventResponseDto> getPublicEvents(Pageable pageable, String kind, String status) {

        EventKind eventKind = Arrays.stream(EventKind.values())
                .filter(type -> type.name().equalsIgnoreCase(kind))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Некорректный параметр kind: " + kind));

        Integer kindInt = (eventKind != EventKind.ALL) ? eventKind.ordinal() : null;
        return getEventByStatus(status, kindInt, pageable, true)
                .map(event -> toDto(event,
                        taggingService.getTagsByEvent(event),
                        authorService.getAuthorName(event),
                        participantService.getParticipant(event.getId()),
                        minioClient));
    }

    @Override
    public Page<EventResponseDto> getPendingApproval(Pageable pageable, String kind, String status) {
        EventKind eventKind = Arrays.stream(EventKind.values())
                .filter(type -> type.name().equalsIgnoreCase(kind))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Некорректный параметр kind: " + kind));
        Integer kindInt = (eventKind != EventKind.ALL) ? eventKind.ordinal() : null;
        return getEventByStatus(status, kindInt, pageable, false)
                .map(event -> toDto(event,
                        taggingService.getTagsByEvent(event),
                        authorService.getAuthorName(event),
                        participantService.getParticipant(event.getId()),
                        minioClient));
    }

    private Page<Event> getEventByStatus(String status, Integer kindInt, Pageable pageable, boolean published) {
        LocalDateTime now = LocalDateTime.now();
        EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());

        boolean isFuture = eventStatus == EventStatus.FUTURE;
        boolean isPast = eventStatus == EventStatus.PAST;

        if (kindInt != null) {
            if (isFuture) {
                return eventRepository.findByPublishedAndKindAndStartedAtAfter(published, kindInt, now, pageable);
            } else if (isPast) {
                return eventRepository.findByPublishedAndKindAndStartedAtBefore(published, kindInt, now, pageable);
            } else {
                return eventRepository.findByPublishedAndKind(published, kindInt, pageable);
            }
        } else {
            if (isFuture) {
                return eventRepository.findByPublishedAndStartedAtAfter(published, now, pageable);
            } else if (isPast) {
                return eventRepository.findByPublishedAndStartedAtBefore(published, now, pageable);
            } else {
                return eventRepository.findByPublished(published, pageable);
            }
        }
    }
}
