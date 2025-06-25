package com.it52.eventregistrationservice.repository;

import com.it52.eventregistrationservice.model.EventParticipation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {
    Optional<EventParticipation> findBySubAndEventId(String sub, Long eventId);
    List<EventParticipation> findBysub(String sub);

}
