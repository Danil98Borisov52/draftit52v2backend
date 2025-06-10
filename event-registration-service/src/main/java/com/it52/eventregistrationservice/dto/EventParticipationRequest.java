package com.it52.eventregistrationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventParticipationRequest {
    private Long eventId;
}
