package com.it52.eventservice.service.api;

import com.it52.eventservice.dto.EventParticipationResponse;

public interface EventRegistrationServiceClient {
    EventParticipationResponse registrationOrganizer(String token, Long eventId);
}
