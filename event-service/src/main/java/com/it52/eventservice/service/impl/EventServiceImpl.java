package com.it52.eventservice.service.impl;

import com.it52.eventservice.config.MinioConfig;
import com.it52.eventservice.dto.EventDto;
import com.it52.eventservice.dto.EventParticipationResponse;
import com.it52.eventservice.dto.EventResponseDto;
import com.it52.eventservice.dto.EventUpdateDto;
import com.it52.eventservice.mapper.EventMapper;
import com.it52.eventservice.model.*;
import com.it52.eventservice.repository.*;
import com.it52.eventservice.service.api.*;
import com.it52.eventservice.util.ReflectionUtils;
import io.minio.MinioClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.it52.eventservice.mapper.EventMapper.toDto;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final MinioClient minioClient;

    private final AuthorService authorService;
    private final TaggingService taggingService;
    private final EventImageService eventImageService;
    private final ParticipantService participantService;
    private final EventRegistrationServiceClient eventRegistrationServiceClient;
    private final MinioConfig minioConfig;
    private final MinioService minioService;

    @Override
    public EventResponseDto createEvent(EventDto dto, MultipartFile image) {
        Author author = authorService.getOrCreateAuthor();

        Event event = createAndSaveEvent(dto, author);
        eventImageService.uploadEventImageIfPresent(image, event);
        List<String> savedTagNames = taggingService.processTags(dto.getTags(), dto.getKind(), event);

        EventResponseDto eventResponseDto = eventMapper.toDto(event, savedTagNames, author.getAuthorName(), null, minioClient);
        //participantService.saveOrganizer(event.getId(), author);
        registerOrganizerToEvent(event.getId());
        kafkaTemplate.send("event_created", eventResponseDto);
        return eventResponseDto;
    }

    private EventParticipationResponse registerOrganizerToEvent(Long eventId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String token = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
        return eventRegistrationServiceClient.registrationOrganizer(token, eventId);
    }

    @Override
    public void approveEvent(String slug) {
        Event event = eventRepository.findBySlug(slug);
        event.setPublished(true);
        eventRepository.save(event);
    }

    @Override
    public void deleteEvent(String slug) {
        eventRepository.deleteBySlug(slug);
    }

    @Override
    public EventResponseDto getEvent(String slug) {
        Event event = eventRepository.findBySlug(slug);
        return toDto(event,
                taggingService.getTagsByEvent(event),
                authorService.getAuthorName(event),
                participantService.getParticipant(event.getId()),
                minioClient);
    }

    @Override
    public EventResponseDto getEventById(Long id) {
        Event event = eventRepository.findById(id).get();
        return toDto(event,
                taggingService.getTagsByEvent(event),
                authorService.getAuthorName(event),
                participantService.getParticipant(id),
                minioClient);
    }

    @Override
    public EventResponseDto updateEvent(String slug, EventUpdateDto dto, MultipartFile image){
        Event event = eventRepository.findBySlug(slug);

        if (dto.getTags() != null) {
            taggingService.processTags(dto.getTags(),dto.getKind(), event);
        }

        ReflectionUtils.mergeNonNullFields(dto, event);

        if (image != null && !image.isEmpty()) {
            String imageUrl = minioService.uploadFile(image, event.getId().toString());
            event.setTitleImage(imageUrl);
        }

        Event saved = eventRepository.save(event);
        return toDto(saved,
                taggingService.getTagsByEvent(event),
                authorService.getAuthorName(event),
                participantService.getParticipant(event.getId()),
                minioClient);
    }


    private Event createAndSaveEvent(EventDto dto, Author author) {
        Event event = eventMapper.create(dto);
        event.setAuthor(author);
        return eventRepository.save(event);
    }
}
