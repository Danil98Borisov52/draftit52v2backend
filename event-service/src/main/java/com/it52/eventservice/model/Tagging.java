package com.it52.eventservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "taggings")
public class Tagging {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", insertable = false, updatable = false)
    private Tag tag;

    @Column(name = "taggable_type")
    private String taggableType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taggable_id", insertable = false, updatable = false)
    private Event taggableId;

    @Column(name = "context")
    private String context;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createAt;
}
