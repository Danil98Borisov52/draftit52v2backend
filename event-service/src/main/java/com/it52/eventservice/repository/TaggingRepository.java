package com.it52.eventservice.repository;

import com.it52.eventservice.model.Tag;
import com.it52.eventservice.model.Tagging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaggingRepository extends JpaRepository<Tagging, Long> {
    List<Tagging> findByTaggableId(Long taggableId);
}
