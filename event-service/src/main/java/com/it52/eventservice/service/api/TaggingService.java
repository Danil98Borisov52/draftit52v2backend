package com.it52.eventservice.service.api;

import com.it52.eventservice.model.Event;

import java.util.List;

public interface TaggingService {
    List<String> processTags(List<String> tagNames, String kind, Event event);
    List<String> getTagsByEvent(Event event);
}
