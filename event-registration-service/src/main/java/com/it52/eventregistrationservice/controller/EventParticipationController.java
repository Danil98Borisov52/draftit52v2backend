package com.it52.eventregistrationservice.controller;


import com.it52.eventregistrationservice.dto.EventParticipationRequest;
import com.it52.eventregistrationservice.dto.EventParticipationResponse;
import com.it52.eventregistrationservice.dto.UserEventsResponse;
import com.it52.eventregistrationservice.service.EventParticipationService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/participations")
public class EventParticipationController {

    private final EventParticipationService participationService;

    public EventParticipationController(EventParticipationService participationService) {
        this.participationService = participationService;
    }

    @PostMapping
    public EventParticipationResponse register(@RequestBody EventParticipationRequest request) {
        var participation = participationService.register(request.getEventId(), request.isOrganizer());

        return EventParticipationResponse.builder()
                .id(participation.getId())
                .sub(participation.getSub())
                .eventId(participation.getEventId())
                .avatarImage(participation.getAvatarImage())
                .organizer(participation.isOrganizer())
                .organizer(participation.isAnonymous())
                .build();
    }

    @GetMapping("/user/{sub}")
    public List<UserEventsResponse> getUserEvents(@PathVariable("sub") String sub) {
        List<UserEventsResponse> participations = participationService.getUserEvents(sub);

        if (participations.isEmpty()) {
            return new ArrayList<>();
        }
        return participations;
    }
}
