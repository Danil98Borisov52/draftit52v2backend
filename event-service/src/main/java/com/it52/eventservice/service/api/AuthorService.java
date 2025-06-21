package com.it52.eventservice.service.api;

import com.it52.eventservice.model.Author;
import com.it52.eventservice.model.Event;

public interface AuthorService {
    Author getOrCreateAuthor();
    String getAuthorName(Event event);
}
