package com.backend.travelid.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueReportModel {
    private int year;
    private int month;
    private String passengerClass;
    private String airline;
    private int numberOfBookings;
    private int numberOfBookingDetails;
    private long totalRevenue;
}

