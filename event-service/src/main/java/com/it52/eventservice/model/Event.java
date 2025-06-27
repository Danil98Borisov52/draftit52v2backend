package com.it52.eventservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "organizer_id")
    private Long organizerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "sub")
    private Author author;

    @Column(name = "published")
    private boolean published;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "title_image")
    private String titleImage;

    @Column(name = "place")
    private String place;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "slug")
    private String slug;

    @Column(name = "type_price_id")
    private Integer typePriceId;

    @Column(name = "status_id")
    private Integer statusId;

    @Column(name = "foreign_link")
    private String foreignLink;

    @Column(name = "kind")
    private Integer kind;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @Column(name = "address_comment")
    private String addressComment;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "taggable_id", referencedColumnName = "id")
    private List<Tagging> taggings;

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