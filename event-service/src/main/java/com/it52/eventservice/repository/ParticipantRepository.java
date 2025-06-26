package com.it52.eventservice.repository;

import com.it52.eventservice.model.Event;
import com.it52.eventservice.model.EventParticipant;
import org.apache.catalina.LifecycleState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<EventParticipant, Long> {
    List<EventParticipant> findAllByEventId(Long eventId);
    List<EventParticipant> findAllBySub(String sub);
}
