package com.it52.eventregistrationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class UserEventsResponse {
    private String sub;
    private String slug;
    private String title;
    private LocalDateTime startedAt;
    private boolean organizer;
}
