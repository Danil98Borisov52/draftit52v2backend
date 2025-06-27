package com.it52.eventservice.service.api;

import com.it52.eventservice.dto.event.EventResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventQueryService {
    Page<EventResponseDTO> getPublicEvents(Pageable pageable, String kind, String status);
    Page<EventResponseDTO> getPendingApproval(Pageable pageable, String kind, String status);
}
