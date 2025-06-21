package com.it52.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipationRequest {
    private Long eventId;
    private boolean organizer;
}