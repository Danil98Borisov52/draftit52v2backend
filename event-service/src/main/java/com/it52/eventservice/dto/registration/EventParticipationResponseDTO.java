package com.it52.eventservice.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationResponseDTO {
    private Long id;
    private String sub;
    private Long eventId;
    private String avatarImage;
}