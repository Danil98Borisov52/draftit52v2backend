package com.it52.eventregistrationservice.client;

public interface EventServiceClient {
    boolean exists(String token, Long eventId);
}
