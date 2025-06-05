package com.it52.eventservice.repository;

import com.it52.eventservice.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByIdIn(List<Long> tagIds);
    Optional<Tag> findByName(String tagName);
}
