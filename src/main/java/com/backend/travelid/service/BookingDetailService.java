package com.backend.travelid.service;

import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.BookingDetail;

import java.util.List;
import java.util.Map;

public interface BookingDetailService {
    List<BookingDetail> getAllBookingDetails();
    Map getByCustomerName(String customerName);
    public Map getByID(Long bookingDetail);
    Map saveBookingDetail(BookingDetail bookingDetail);
    Map updateBookingDetail(BookingDetail bookingDetail);
    Map deleteBookingDetail(BookingDetail bookingDetail);
}
