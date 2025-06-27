package com.it52.eventservice.service.api;

import com.it52.eventservice.model.Address;

import java.util.List;

public interface AddressService {
    Address getOrCreateAddress(List<Double> coords);
}
