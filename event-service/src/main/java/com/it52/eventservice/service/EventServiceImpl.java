package com.it52.eventservice.service;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.dto.EventResponseDto;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            TaggingRepository taggingRepository, TagRepository tagRepository,
                            AuthorRepository authorRepository) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.taggingRepository = taggingRepository;
        this.tagRepository = tagRepository;
        this.authorRepository = authorRepository;
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
        EventResponseDto eventResponseDto = eventMapper.toDto(saved, tagNames, authorName);
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
        return getEventByStatus(status, kindInt, pageable)
                .map(event -> toDto(event, getTagsByEvent(event), getAuthorName(event)));
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

    private Page<Event> getEventByStatus(String status, Integer kindInt, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());
        switch (eventStatus) {
            case FUTURE:
                return kindInt != null
                        ? eventRepository.findByPublishedTrueAndKindAndStartedAtAfter(kindInt, now, pageable)
                        : eventRepository.findByPublishedTrueAndStartedAtAfter(now, pageable);
            case PAST:
                return kindInt != null
                        ? eventRepository.findByPublishedTrueAndKindAndStartedAtBefore(kindInt, now, pageable)
                        : eventRepository.findByPublishedTrueAndStartedAtBefore(now, pageable);
            default:
                return kindInt != null
                        ? eventRepository.findByPublishedTrueAndKind(kindInt, pageable)
                        : eventRepository.findByPublishedTrue(pageable);
        }
    }

    @Override
    public EventResponseDto getEvent(String slug) {
        Event event = eventRepository.findBySlug(slug);
        return toDto(event, getTagsByEvent(event), getAuthorName(event));
    }

    @Override
    public EventResponseDto getEventById(Long id){
        Event event = eventRepository.findById(id).get();
        return toDto(event, getTagsByEvent(event), getAuthorName(event));
    }

    @Override
    public List<Event> getPendingApproval() {
        return eventRepository.findByPublishedFalse();
    }

    @Override
    public void approveEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        event.setPublished(true);
        eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
