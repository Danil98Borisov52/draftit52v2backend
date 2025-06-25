package com.it52.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserEventParticipationDTO{
    private String sub;
    private String title;
    private LocalDateTime startedAt;
    private String slug;
    private boolean organizer;
}
