package com.it52.eventregistrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EventParticipationResponse {
    private Long id;
    private String sub;
    private String slug;
    private Long eventId;
    private String avatarImage;
    private boolean organizer;
}
