package com.it52.eventregistrationservice.client;

import com.it52.eventregistrationservice.dto.UserResponseDTO;

public interface UserServiceClient {
    UserResponseDTO getBySub(String token, String sub);
}
