package com.it52.eventregistrationservice.controller;


import com.it52.eventregistrationservice.dto.EventParticipationRequest;
import com.it52.eventregistrationservice.dto.EventParticipationResponse;
import com.it52.eventregistrationservice.service.EventParticipationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/participations")
public class EventParticipationController {

    private final EventParticipationService participationService;

    public EventParticipationController(EventParticipationService participationService) {
        this.participationService = participationService;
    }

    @PostMapping
    public ResponseEntity<EventParticipationResponse> register(@RequestBody EventParticipationRequest request) {
        var participation = participationService.register(request.getEventId());

        var response = new EventParticipationResponse(
                participation.getId(),
                participation.getSub(),
                participation.getEventId(),
                participation.getAvatarImage());

        return ResponseEntity.ok(response);
    }
}
