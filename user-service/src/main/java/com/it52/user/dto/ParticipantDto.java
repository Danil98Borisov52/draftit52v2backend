package com.it52.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ParticipantDto {
    private String slug;
    private Long eventId;
    private String sub;
    private String userName;
    private LocalDateTime registeredAt;
    private String email;
    private String username;
    private String firstName;
    private String eventTitle;
    private LocalDateTime eventDate;
    private String eventPlace;
    private String avatarImage;
    private boolean organizer;
}
