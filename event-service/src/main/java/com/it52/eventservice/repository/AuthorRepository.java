package com.it52.eventservice.repository;

import com.it52.eventservice.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, String> {
    Optional<Author> findBySub(String sub);
}
