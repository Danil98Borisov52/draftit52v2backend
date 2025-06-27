package com.it52.eventservice.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipationRequestDTO {
    private Long eventId;
    private boolean organizer;
}