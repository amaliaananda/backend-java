package com.backend.travelid.repository;

import com.backend.travelid.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> , JpaSpecificationExecutor<Flight> {

    List<Flight> findByAirline(String airline);
    List<Flight> findByPassengerClass(String passengerClass);
    Optional<Flight> findById(Long id);
}

