package com.backend.travelid.service;


import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.Flight;
import com.backend.travelid.entity.Seat;

import java.util.List;
import java.util.Map;

public interface SeatService {
    void saveSeat(Flight flight, String seatBooked);
    Map getByID(Long idSeat);
    List<Seat> getByFlight(Long flightId);
}

