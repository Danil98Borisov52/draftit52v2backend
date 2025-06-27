package com.it52.eventregistrationservice.client;

import com.it52.eventregistrationservice.dto.EventResponseDTO;

public interface EventServiceClient {
     EventResponseDTO getEventById(String token, Long eventId);
}
