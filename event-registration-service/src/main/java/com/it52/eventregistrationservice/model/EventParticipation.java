package com.it52.eventregistrationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "event_participations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sub", "event_id"})
})
public class EventParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sub", nullable = false)
    private String sub;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "title")
    private String title;

    @Column(name ="slug")
    private String slug;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name ="avatarImage")
    private String avatarImage;

    @Column(name = "organizer")
    private boolean organizer;

    public EventParticipation(String sub, Long eventId) {
        this.sub = sub;
        this.eventId = eventId;
    }
    public EventParticipation(String sub, Long eventId, String avatarImage, boolean organizer) {
        this.sub = sub;
        this.eventId = eventId;
        this.avatarImage = avatarImage;
        this.organizer = organizer;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}