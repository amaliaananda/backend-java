package com.backend.travelid.service.impl;

import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.BookingDetail;
import com.backend.travelid.request.RevenueReportModel;
import com.backend.travelid.service.BookingDetailService;
import com.backend.travelid.service.BookingService;
import com.backend.travelid.service.RevenueReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RevenueReportServiceImpl implements RevenueReportService {

    @Autowired
    private BookingDetailService bookingDetailService;

    @Autowired
    private BookingService bookingService;

    public RevenueReportModel generateMonthlyRevenueReport(int year, int month, String passengerClass, String airline) {
        try {
            log.info("generate Monthly Revenue Report");
            // Retrieve booking data for the specified month and year
            List<Booking> bookings = bookingService.findByYearAndMonth(year, month);
            List<BookingDetail> bookingDetails = bookingDetailService.findByYearAndMonth(year, month);

            // Filter bookings based on passenger class if specified
            if (passengerClass != null && !passengerClass.isEmpty()) {
                bookingDetails = bookingDetails.stream()
                        .filter(bookingDetail -> bookingDetail.getFlight().getPassengerClass().equalsIgnoreCase(passengerClass))
                        .collect(Collectors.toList());
            }
            if (airline != null && !airline.isEmpty()) {
                bookingDetails = bookingDetails.stream()
                        .filter(bookingDetail -> bookingDetail.getFlight().getAirlines().getAirline().equalsIgnoreCase(airline))
                        .collect(Collectors.toList());
            }
            // Calculate total revenue
            long totalRevenue = bookingDetails.stream()
                    .mapToLong(BookingDetail::getPrice)
                    .sum();
            // Return monthly revenue report
            return new RevenueReportModel (year, month, passengerClass, airline, bookings.size(), bookingDetails.size(), totalRevenue);
        } catch (Exception e) {
            log.error("generate Monthly Revenue Report error: " + e.getMessage());
            throw new RuntimeException("generate Monthly Revenue Report =" + e.getMessage());
        }
    }

    public RevenueReportModel generateAnnualRevenueReport(int year, String passengerClass, String airline) {
        try {
            log.info("generate Annual Revenue Report");
            int month = 13;
            // Retrieve booking data for the specified year
            List<Booking> bookings = bookingService.findByYear(year);
            List<BookingDetail> bookingDetails = bookingDetailService.findByYear(year);

            // Filter bookings based on passenger class if specified
            if (passengerClass != null && !passengerClass.isEmpty()) {
                bookingDetails = bookingDetails.stream()
                        .filter(bookingDetail -> bookingDetail.getFlight().getPassengerClass().equalsIgnoreCase(passengerClass))
                        .collect(Collectors.toList());
            }
            if (airline != null && !airline.isEmpty()) {
                bookingDetails = bookingDetails.stream()
                        .filter(bookingDetail -> bookingDetail.getFlight().getAirlines().getAirline().equalsIgnoreCase(airline))
                        .collect(Collectors.toList());
            }
            // Calculate total revenue
            long totalRevenue = bookingDetails.stream()
                    .mapToLong(BookingDetail::getPrice)
                    .sum();
            // Return annual revenue report
            return new RevenueReportModel (year, month, passengerClass, airline, bookings.size(), bookingDetails.size(), totalRevenue);
        } catch (Exception e) {
            log.error("generate Annual Revenue Report error: " + e.getMessage());
            throw new RuntimeException("generate Annual Revenue Report =" + e.getMessage());
        }
    }
}
