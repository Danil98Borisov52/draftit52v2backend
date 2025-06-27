package com.it52.eventservice.service.impl;

import com.it52.eventservice.model.Address;
import com.it52.eventservice.repository.AddressRepository;
import com.it52.eventservice.service.api.AddressService;
import jakarta.ws.rs.core.Link;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    @Override
    public Address getOrCreateAddress(List<Double> coords) {
        Double latitude = coords.get(0);
        Double longitude = coords.get(1);
        return addressRepository.findByLatitudeAndLongitude(latitude, longitude)
                .orElseGet(() -> addressRepository.save(
                        Address.builder()
                                .latitude(latitude)
                                .longitude(longitude)
                                .unrestrictedValue("Нижегородская обл, г Нижний Новгород, Нижегородский р-н, ул Алексеевская, д 3")
                                .city("Нижний Новгород")
                                .street("ул Алексеевская")
                                .house("дом 3")
                                .kladrId("5200000100000220009")
                                .fiasId("c2bb3da8-8a1c-4c9a-9c01-264866413d17")
                                .build()));
    }
}
