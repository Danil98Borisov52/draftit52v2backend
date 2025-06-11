package com.it52.eventregistrationservice.client;

import com.it52.eventregistrationservice.dto.UserDTO;

public interface UserServiceClient {
    UserDTO getBySub(String token, String sub);

}
