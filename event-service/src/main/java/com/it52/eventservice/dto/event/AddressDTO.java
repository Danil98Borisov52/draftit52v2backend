package com.it52.eventservice.dto.event;

import lombok.Data;

import java.util.List;

@Data
public class AddressDTO {
    private String unrestrictedValue;
    private String city;
    private String street;
    private String house;
    private List<Double> coords;
}
