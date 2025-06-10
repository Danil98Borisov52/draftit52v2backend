package com.it52.eventregistrationservice.client;

public interface UserServiceClient {
    boolean exists(String token, String sub);

}
