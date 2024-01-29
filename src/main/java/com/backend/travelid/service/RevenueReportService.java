package com.backend.travelid.service;


import com.backend.travelid.entity.Flight;
import com.backend.travelid.entity.Seat;
import com.backend.travelid.request.RevenueReportModel;

import java.util.List;
import java.util.Map;

public interface RevenueReportService {
    RevenueReportModel generateMonthlyRevenueReport(int year, int month, String passengerClass, String airline);
    RevenueReportModel generateAnnualRevenueReport(int year, String passengerClass, String airline);
}

