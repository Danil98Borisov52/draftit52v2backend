package com.it52.eventservice.service.api;

import com.it52.eventservice.model.Author;
import com.it52.eventservice.model.EventParticipant;

import java.util.List;

public interface ParticipantService {
    void listenUserRegisteredToEvent(String message);
    List<EventParticipant> getParticipant(Long eventId);
}
