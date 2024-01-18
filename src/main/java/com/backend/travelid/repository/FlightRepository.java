package com.backend.travelid.repository;

import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> , JpaSpecificationExecutor<Flight> {

    List<Flight> findByPassengerClass(String passengerClass);
    Optional<Flight> findById(Long id);

    @Query("SELECT f FROM Flight f JOIN f.airlines a WHERE a.airline = :airline")
    public List<Flight> getByAirline(@Param("airline") String airline);
}

