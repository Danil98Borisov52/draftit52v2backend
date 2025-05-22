package com.it52.eventservice.repository;

import com.it52.eventservice.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByPublishedTrue();
    List<Event> findByApprovedFalse();
}