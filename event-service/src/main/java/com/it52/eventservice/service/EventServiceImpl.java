package com.it52.eventservice.service;

import com.it52.eventservice.dto.EventCreateDto;
import com.it52.eventservice.mapper.EventMapper;
import com.it52.eventservice.model.Event;
import com.it52.eventservice.repository.EventRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventServiceImpl(EventRepository eventRepository, EventMapper eventMapper,
                            KafkaTemplate<String, Object> kafkaTemplate) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public EventCreateDto createEvent(EventCreateDto dto) {
        Event event = eventMapper.toEntity(dto);
        Event saved = eventRepository.save(event);

        kafkaTemplate.send("event_created", saved);

        return dto;
    }

    @Override
    public List<Event> getPublicEvents() {
        return eventRepository.findByPublishedTrue();
    }

    @Override
    public List<Event> getPendingApproval() {
        return eventRepository.findByApprovedFalse();
    }

    @Override
    public void approveEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
                        event.setApproved(true);
        event.setPublished(true);
        eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
