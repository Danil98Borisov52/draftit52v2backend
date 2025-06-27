package com.it52.eventregistrationservice.controller;


import com.it52.eventregistrationservice.dto.EventParticipationRequestDTO;
import com.it52.eventregistrationservice.dto.EventParticipationResponseDTO;
import com.it52.eventregistrationservice.dto.ParticipationResponseDTO;
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
    public EventParticipationResponseDTO register(@RequestBody EventParticipationRequestDTO request) {
        var participation = participationService.register(request.getEventId(), request.isOrganizer());

        return EventParticipationResponseDTO.builder()
                .id(participation.getId())
                .sub(participation.getSub())
                .eventId(participation.getEventId())
                .avatarImage(participation.getAvatarImage())
                .organizer(participation.isOrganizer())
                .anonymous(participation.isAnonymous())
                .build();
    }

    @GetMapping("/user/{sub}")
    public List<ParticipationResponseDTO> getUserEvents(@PathVariable("sub") String sub) {
        List<ParticipationResponseDTO> participations = participationService.getUserEvents(sub);

        if (participations.isEmpty()) {
            return new ArrayList<>();
        }
        return participations;
    }
}
