package com.it52.eventservice.repository;

import com.it52.eventservice.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    Optional<Address> findByLatitudeAndLongitude(Double latitude, Double longitude);
}
