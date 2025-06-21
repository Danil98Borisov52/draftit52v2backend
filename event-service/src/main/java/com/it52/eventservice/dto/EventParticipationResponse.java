package com.it52.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationResponse {
    private Long id;
    private String sub;
    private Long eventId;
    private String avatarImage;
}