package com.it52.eventservice.repository;

import com.it52.eventservice.model.Tagging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaggingRepository extends JpaRepository<Tagging, Long> {
}
