package com.it52.eventservice.dto.registration;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipantDto {
    private Long eventId;
    private String sub;
    private String slug;
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
    private boolean anonymous;
}
