package com.backend.travelid.repository;


import com.backend.travelid.entity.Airline;
import com.backend.travelid.entity.oauth.Client;
import com.backend.travelid.entity.oauth.Role;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface AirlineRepository extends PagingAndSortingRepository<Airline, Long> {
    Airline findOneByAirline(String airline);
    Optional<Airline> findByAirline(String airline);
}

