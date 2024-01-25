package com.backend.travelid.service;

import com.backend.travelid.entity.Flight;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface FlightService {
    List<Flight> getFlightsByAirline(String airline);
    List<Flight> getFlightsByPassengerClass(String passengerClass);
    Map getByID(Long booking);
    Map saveFlight(Flight flight);
    Map updateFlight(Flight flight);
    Map deleteFlight(Flight flight);
}
