package com.backend.travelid.service;

import com.backend.travelid.dto.BookingRequestDTO;
import com.backend.travelid.dto.PaymentRequestDTO;
import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.Customer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingService {
    List<Booking> getAllBookings();
    Map getByCustomerId(Long customerId);
    public Map getByID(Long booking);
    Map saveBooking(Booking booking);
    Map updateBooking(Booking booking);
    Map deleteBooking(Booking booking);
    Map processPayment(PaymentRequestDTO paymentRequestDTO);
    Map saveBookingWithDetails(BookingRequestDTO bookingRequestDTO);
}
