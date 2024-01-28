package com.backend.travelid.repository;


import com.backend.travelid.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long>, JpaSpecificationExecutor<Seat> {
    List <Seat> getByFlight(Optional<Flight> flight);
    Optional<Seat> getByFlightAndSeatBooked(Flight flight, String seatNumber);
}

