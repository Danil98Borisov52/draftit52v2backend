package com.it52.user.service.api;

import com.it52.user.dto.UserEventParticipationDTO;

import java.util.List;

public interface EventRegistrationServiceClient {
    List<UserEventParticipationDTO> getUserEvents(String sub);
}
