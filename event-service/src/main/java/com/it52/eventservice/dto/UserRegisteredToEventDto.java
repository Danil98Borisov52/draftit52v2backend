package com.it52.eventservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRegisteredToEventDto {
    private String sub;
    private Long eventId;
    private String email;
    private String username;
    private String firstName;
    private String avatarImage;
    private String eventTitle;
    private LocalDateTime eventDate;
    private String eventPlace;
    private LocalDateTime registeredAt;
    private boolean organizer;
}
