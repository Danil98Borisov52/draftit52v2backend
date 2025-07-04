package com.it52.notificationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRegisteredToEventDto {
    private String email;
    private String username;
    private String firstName;
    private String eventTitle;
    private LocalDateTime eventDate;
    private String eventPlace;
    private boolean organizer;
}
