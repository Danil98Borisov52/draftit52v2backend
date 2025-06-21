package com.it52.eventservice.service.impl;

import com.it52.eventservice.model.Event;
import com.it52.eventservice.model.Tag;
import com.it52.eventservice.model.Tagging;
import com.it52.eventservice.repository.TagRepository;
import com.it52.eventservice.repository.TaggingRepository;
import com.it52.eventservice.service.api.TaggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TaggingServiceImpl implements TaggingService {

    private final TaggingRepository taggingRepository;
    private final TagRepository tagRepository;

    @Override
    public List<String> processTags(List<String> tagNames, String kind, Event event) {
        List<String> savedTagNames = new ArrayList<>();

        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                String cleanName = tagName.trim();

                Tag tag = tagRepository.findByName(cleanName)
                        .orElseGet(() -> tagRepository.save(
                                Tag.builder()
                                        .name(cleanName)
                                        .taggingsCount(0L)
                                        .build()
                        ));

                taggingRepository.save(Tagging.builder()
                        .tag(tag)
                        .taggableType(kind)
                        .taggable(event)
                        .context("tags")
                        .createAt(LocalDate.now())
                        .build());

                tag.setTaggingsCount(tag.getTaggingsCount() + 1);
                tagRepository.save(tag);

                savedTagNames.add(tag.getName());
            }
        }

        return savedTagNames;
    }
    @Override
    public List<String> getTagsByEvent(Event event) {
        return event.getTaggings().stream()
                .map(Tagging::getTag)
                .filter(Objects::nonNull)
                .map(tag -> tag.getName())
                .toList();
    }
}
