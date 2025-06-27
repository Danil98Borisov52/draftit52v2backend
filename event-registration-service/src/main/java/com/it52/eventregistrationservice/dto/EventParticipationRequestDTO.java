package com.it52.eventregistrationservice.dto;

import lombok.Data;

@Data
public class EventParticipationRequestDTO {
    private Long eventId;
    private boolean organizer;
}
