package com.it52.eventservice.service.impl;

import com.it52.eventservice.config.ImageProxyConfig;
import com.it52.eventservice.config.MinioConfig;
import com.it52.eventservice.dto.event.EventRequestDTO;
import com.it52.eventservice.dto.registration.EventParticipationResponseDTO;
import com.it52.eventservice.dto.event.EventResponseDTO;
import com.it52.eventservice.dto.event.EventUpdateRequestDTO;
import com.it52.eventservice.exception.EventNotFoundException;
import com.it52.eventservice.mapper.EventMapper;
import com.it52.eventservice.model.*;
import com.it52.eventservice.repository.*;
import com.it52.eventservice.service.api.*;
import com.it52.eventservice.util.ReflectionUtils;
import io.minio.MinioClient;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
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
    private final AddressService addressService;
    private final ImageProxyConfig imageProxyConfig;

    @Override
    public EventResponseDTO createEvent(EventRequestDTO dto, MultipartFile image) {
        Author author = authorService.getOrCreateAuthor();
        Address address = addressService.getOrCreateAddress(dto.getCoords());

        Event event = createAndSaveEvent(dto, author, address);
        eventImageService.uploadEventImageIfPresent(image, event);
        List<String> tags = dto.getTags();
        if (tags == null || tags.isEmpty()) {
            throw new BadRequestException("Список тегов не может быть пустым.");
        }
        List<String> savedTagNames = taggingService.processTags(dto.getTags(), dto.getKind(), event);
        registerOrganizerToEvent(event.getId());
        EventResponseDTO eventResponseDto = eventMapper.toDto(event,
                savedTagNames,
                author.getAuthorName(),
                participantService.getParticipant(event.getId()),
                address,
                minioConfig,
                imageProxyConfig);
        kafkaTemplate.send("event_created", eventResponseDto);
        return eventResponseDto;
    }

    private EventParticipationResponseDTO registerOrganizerToEvent(Long eventId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication instanceof JwtAuthenticationToken jwtAuth)) {
            throw new IllegalStateException("Пользователь не аутентифицирован или токен недоступен");
        }
        String token = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();
        return eventRegistrationServiceClient.registrationOrganizer(token, eventId);
    }

    @Override
    public void approveEvent(String slug) {
        Event event = eventRepository.findBySlug(slug);
        validateEvent(event, slug);
        event.setPublished(true);
        event.setPublishedAt(LocalDateTime.now());
        eventRepository.save(event);
    }

    @Override
    public void deleteEvent(String slug) {
        Event event = eventRepository.findBySlug(slug);
        validateEvent(event, slug);
        eventRepository.deleteBySlug(slug);
    }

    @Override
    public EventResponseDTO getEvent(String slug) {
        Event event = eventRepository.findBySlug(slug);
        validateEvent(event, slug);
        return toDto(event,
                taggingService.getTagsByEvent(event),
                authorService.getAuthorName(event),
                participantService.getParticipant(event.getId()),
                event.getAddress(),
                minioConfig,
                imageProxyConfig);
    }

    @Override
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id).get();
        if (event == null) {
            throw new EventNotFoundException("Событие с id '" + id + "' не найдено.");
        }
        return toDto(event,
                taggingService.getTagsByEvent(event),
                authorService.getAuthorName(event),
                participantService.getParticipant(id),
                event.getAddress(),
                minioConfig,
                imageProxyConfig);
    }

    @Override
    public EventResponseDTO updateEvent(String slug, EventUpdateRequestDTO dto, MultipartFile image) {
        Event event = eventRepository.findBySlug(slug);
        validateEvent(event, slug);
        if (dto.getTags() != null) {
            taggingService.processTags(dto.getTags(), dto.getKind(), event);
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
                event.getAddress(),
                minioConfig,
                imageProxyConfig);
    }


    private Event createAndSaveEvent(EventRequestDTO dto, Author author, Address address) {
        Event event = eventMapper.create(dto);
        event.setAuthor(author);
        event.setAddress(address);
        return eventRepository.save(event);
    }

    private void validateEvent(Event event, String slug) {
        if (event == null) {
            throw new EventNotFoundException("Событие с slug '" + slug + "' не найдено.");
        }
    }
}
