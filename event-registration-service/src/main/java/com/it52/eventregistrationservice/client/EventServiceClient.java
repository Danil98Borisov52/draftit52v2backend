package com.it52.eventregistrationservice.client;

import com.it52.eventregistrationservice.dto.EventDto;

public interface EventServiceClient {
     EventDto getEventById(String token, Long eventId);
}
