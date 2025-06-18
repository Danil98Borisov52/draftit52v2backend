package com.it52.eventservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.dto.EventResponseDto;
import com.it52.eventservice.dto.ParticipantDto;
import com.it52.eventservice.mapper.EventMapper;
import com.it52.eventservice.model.*;
import com.it52.eventservice.repository.*;
import com.it52.eventservice.enums.EventKind;
import com.it52.eventservice.enums.EventStatus;
import com.it52.eventservice.util.SecurityUtils;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.it52.eventservice.mapper.EventMapper.toDto;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    private final EventRepository eventRepository;
    private final TaggingRepository taggingRepository;
    private final TagRepository tagRepository;
    private final AuthorRepository authorRepository;
    private final EventMapper eventMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ParticipantRepository participantRepository;
    private final ObjectMapper objectMapper;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            TaggingRepository taggingRepository, TagRepository tagRepository,
                            AuthorRepository authorRepository,
                            ParticipantRepository participantRepository,
                            ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.taggingRepository = taggingRepository;
        this.tagRepository = tagRepository;
        this.authorRepository = authorRepository;
        this.participantRepository = participantRepository;
        this.objectMapper = objectMapper;
    }



    @Override
    @Transactional
    public EventResponseDto createEvent(EventCreateDto dto) {
        String sub  = SecurityUtils.getCurrentUserId();
        String authorName = SecurityUtils.getCurrentUsername();
        String email = SecurityUtils.getCurrentUserEmail();

        Author author = authorRepository.findBySub(sub)
                .orElseGet(() -> {
                Author newAuthor = Author.builder()
                    .sub(sub)
                    .authorName(authorName)
                    .email(email)
                    .build();
            return authorRepository.save(newAuthor);
        });

        Event event = eventMapper.create(dto);
        event.setAuthor(author);
        Event saved = eventRepository.save(event);

        List<String> tagNames = dto.getTags();
        List<String> savedTagNames = new ArrayList<>();

        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                String cleanName = tagName.trim();

                Tag tag = tagRepository.findByName(cleanName)
                        .orElseGet(() -> {
                            Tag newTag = Tag.builder()
                                    .name(cleanName)
                                    .taggingsCount(0L)
                                    .build();
                            return tagRepository.save(newTag);
                        });

                Tagging tagging = Tagging.builder()
                        .tag(tag)
                        .taggableType(dto.getKind())
                        .taggable(saved)
                        .context("tags")
                        .createAt(LocalDate.now())
                        .build();

                taggingRepository.save(tagging);

                tag.setTaggingsCount(tag.getTaggingsCount() + 1);
                tagRepository.save(tag);

                savedTagNames.add(tag.getName());
            }
        }
        EventResponseDto eventResponseDto = eventMapper.toDto(saved, tagNames, authorName, null);
        kafkaTemplate.send("event_created", eventResponseDto);
        return eventResponseDto;
    }

    @Override
    public Page<EventResponseDto> getPublicEvents(Pageable pageable, String kind, String status) {

        EventKind eventKind = Arrays.stream(EventKind.values())
                .filter(type -> type.name().equalsIgnoreCase(kind))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Некорректный параметр kind: " + kind));

        Integer kindInt = (eventKind != EventKind.ALL) ? eventKind.ordinal() : null;
        return getEventByStatus(status, kindInt, pageable, true)
                .map(event -> toDto(event, getTagsByEvent(event), getAuthorName(event), getParticipant(event.getId())));
    }

    private List<EventParticipant> getParticipant(Long eventId){
        return participantRepository.findAllByEventId(eventId);
    }
    private List<String> getTagsByEvent(Event event) {
        return event.getTaggings().stream()
                .map(Tagging::getTag)
                .filter(Objects::nonNull)
                .map(tag -> tag.getName())
                .toList();
    }

    private String getAuthorName(Event event){
        if (event.getAuthor() == null) {
            return "Автор остался в другой таблице";
            //throw new RuntimeException("У события id=" + event.getId() + " не задан автор");
        }

        String sub = event.getAuthor().getSub();
        Author author = authorRepository.findBySub(sub)
                .orElseThrow(() -> new RuntimeException("Автор с sub=" + sub + " не найден"));

        return author.getAuthorName();
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

    @Override
    @KafkaListener(topics = "user_registered_to_event", groupId = "event-group")
    public void listenUserRegisteredToEvent(String message) {
        try {
            ParticipantDto participantDto = objectMapper.readValue(message, ParticipantDto.class);
            logger.info("Получено событие: {}", message);

            EventParticipant participant = new EventParticipant();
            participant.setEventId(participantDto.getEventId());
            participant.setSub(participantDto.getSub());
            participant.setRegisteredAt(participantDto.getRegisteredAt());
            participant.setAvatarImage(participantDto.getAvatarImage());

            participantRepository.save(participant);
            logger.info("Участник сохранён: {}", participant);
        } catch (Exception e) {
            logger.error("Ошибка обработки Kafka-сообщения: {}", message, e);
        }
    }

    @Override
    public EventResponseDto getEvent(String slug) {
        Event event = eventRepository.findBySlug(slug);
        return toDto(event, getTagsByEvent(event), getAuthorName(event), getParticipant(event.getId()));
    }

    @Override
    public EventResponseDto getEventById(Long id){
        Event event = eventRepository.findById(id).get();
        return toDto(event, getTagsByEvent(event), getAuthorName(event), getParticipant(id));
    }

    @Override
    public Page<EventResponseDto> getPendingApproval(Pageable pageable, String kind, String status) {
        EventKind eventKind = Arrays.stream(EventKind.values())
                .filter(type -> type.name().equalsIgnoreCase(kind))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Некорректный параметр kind: " + kind));
        Integer kindInt = (eventKind != EventKind.ALL) ? eventKind.ordinal() : null;
        return getEventByStatus(status, kindInt, pageable, false)
                .map(event -> toDto(event, getTagsByEvent(event), getAuthorName(event), getParticipant(event.getId())));
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
}
