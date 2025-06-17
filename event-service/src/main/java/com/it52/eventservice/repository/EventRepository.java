package com.it52.eventservice.repository;

import com.it52.eventservice.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByPublishedTrue(Pageable pageable);

    List<Event> findByPublishedFalse();

    Event findBySlug(String slug);

    Page<Event> findByPublishedTrueAndStartedAtAfter(LocalDateTime now, Pageable pageable);

    Page<Event> findByPublishedTrueAndStartedAtBefore(LocalDateTime now, Pageable pageable);

    Page<Event> findByPublishedTrueAndKindAndStartedAtAfter(Integer kind, LocalDateTime now, Pageable pageable);

    Page<Event> findByPublishedTrueAndKindAndStartedAtBefore(Integer kind, LocalDateTime now, Pageable pageable);

    Page<Event> findByPublishedTrueAndKind(Integer kind, Pageable pageable);

    void deleteBySlug(String slug);
}