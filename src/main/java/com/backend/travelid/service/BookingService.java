package com.backend.travelid.service;

import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.Customer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingService {
    List<Booking> getAllBookings();
    Map getByCustomerId(Long CustomerId);
    public Map getByID(Long booking);
    Map saveBooking(Booking booking);
    Map updateBooking(Booking booking);
    Map deleteBooking(Booking booking);
}
