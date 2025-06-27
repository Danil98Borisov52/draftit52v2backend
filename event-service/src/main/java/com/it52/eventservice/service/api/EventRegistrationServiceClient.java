package com.it52.eventservice.service.api;

import com.it52.eventservice.dto.registration.EventParticipationResponseDTO;

public interface EventRegistrationServiceClient {
    EventParticipationResponseDTO registrationOrganizer(String token, Long eventId);
}
