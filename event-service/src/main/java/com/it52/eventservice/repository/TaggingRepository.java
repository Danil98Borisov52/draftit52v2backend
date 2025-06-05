package com.it52.eventservice.repository;

import com.it52.eventservice.model.Tag;
import com.it52.eventservice.model.Tagging;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaggingRepository extends JpaRepository<Tagging, Long> {
    List<Tagging> findByTaggableId(Long taggableId);
}
