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
    private int numberOfBookings;
    private long totalRevenue;
}

