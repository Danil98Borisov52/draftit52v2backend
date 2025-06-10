package com.it52.eventregistrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventParticipationResponse {
    private Long id;
    private String sub;
    private Long eventId;
}
