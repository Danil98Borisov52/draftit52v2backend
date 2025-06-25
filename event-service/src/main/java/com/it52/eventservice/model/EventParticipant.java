package com.it52.eventservice.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "event_participants_projection", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sub", "event_id"})
})
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="sub")
    private String sub;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name ="slug")
    private String slug;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "avatar_image")
    private String avatarImage;

    @Column(name = "organizer")
    private boolean organizer;
}
