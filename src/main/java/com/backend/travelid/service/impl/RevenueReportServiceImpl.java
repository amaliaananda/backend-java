package com.backend.travelid.service.impl;

import com.backend.travelid.entity.Booking;
import com.backend.travelid.request.RevenueReportModel;
import com.backend.travelid.service.BookingService;
import com.backend.travelid.service.RevenueReportService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RevenueReportServiceImpl implements RevenueReportService {

    @Autowired
    private BookingService bookingService;

    public RevenueReportModel generateMonthlyRevenueReport(int year, int month, String passengerClass) {
        try {
            log.info("generate Monthly Revenue Report");
            // Retrieve booking data for the specified month and year
            List<Booking> bookings = bookingService.findByYearAndMonth(year, month);

            // Filter bookings based on passenger class if specified
            if (passengerClass != null && !passengerClass.isEmpty()) {
                bookings = bookings.stream()
                        .filter(booking -> booking.getBookingDetail().stream()
                                .anyMatch(bookingDetail -> bookingDetail.getFlight().getPassengerClass().equalsIgnoreCase(passengerClass)))
                        .collect(Collectors.toList());
            }
            // Calculate total revenue
            long totalRevenue = bookings.stream()
                    .mapToLong(Booking::getTotalPrice)
                    .sum();
            // Return monthly revenue report
            return new RevenueReportModel (year, month, passengerClass, bookings.size(), totalRevenue);
        } catch (Exception e) {
            log.error("generate Monthly Revenue Report error: " + e.getMessage());
            throw new RuntimeException("generate Monthly Revenue Report =" + e.getMessage());
        }
    }

    public RevenueReportModel generateAnnualRevenueReport(int year, String passengerClass) {
        try {
            log.info("generate Annual Revenue Report");
            // Retrieve booking data for the specified year
            List<Booking> bookings = bookingService.findByYear(year);

            // Filter bookings based on passenger class if specified
            if (passengerClass != null && !passengerClass.isEmpty()) {
                bookings = bookings.stream()
                        .filter(booking -> booking.getBookingDetail().stream()
                                .anyMatch(bookingDetail -> bookingDetail.getFlight().getPassengerClass().equalsIgnoreCase(passengerClass)))
                        .collect(Collectors.toList());
            }
            // Calculate total revenue
            long totalRevenue = bookings.stream()
                    .mapToLong(Booking::getTotalPrice)
                    .sum();
            // Return annual revenue report
            return new RevenueReportModel (year, passengerClass, bookings.size(), totalRevenue);
        } catch (Exception e) {
            log.error("generate Annual Revenue Report error: " + e.getMessage());
            throw new RuntimeException("generate Annual Revenue Report =" + e.getMessage());
        }
    }
}
