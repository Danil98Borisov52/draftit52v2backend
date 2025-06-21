package com.it52.eventservice.service.api;

import com.it52.eventservice.dto.EventResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventQueryService {
    Page<EventResponseDto> getPublicEvents(Pageable pageable, String kind, String status);
    Page<EventResponseDto> getPendingApproval(Pageable pageable, String kind, String status);
}
