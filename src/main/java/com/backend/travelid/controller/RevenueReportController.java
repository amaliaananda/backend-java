package com.backend.travelid.controller;

import com.backend.travelid.entity.Seat;
import com.backend.travelid.repository.SeatRepository;
import com.backend.travelid.request.RevenueReportModel;
import com.backend.travelid.service.RevenueReportService;
import com.backend.travelid.service.SeatService;
import com.backend.travelid.utils.SimpleStringUtils;
import com.backend.travelid.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/revenueReport")
public class RevenueReportController {

    @Autowired
    private RevenueReportService revenueReportService;

    @Autowired
    public TemplateResponse response;

    @GetMapping(value={"/monthly", "/monthly/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> generateMonthlyRevenueReport(@RequestParam(required = true) int year,
                                                            @RequestParam(required = true) int month,
                                                            @RequestParam(required = false) String passengerClass,
                                                            @RequestParam(required = false) String airline) {
        try {
            RevenueReportModel monthlyReport = revenueReportService.generateMonthlyRevenueReport(year, month, passengerClass, airline);
            Map map = new HashMap();
            map.put("data",monthlyReport);
            map.put("message", "sukses");
            map.put("status", 200);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @GetMapping(value={"/annual", "/annual/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> generateAnnualRevenueReport(@RequestParam(required = true) int year,
                                                           @RequestParam(required = false) String passengerClass,
                                                           @RequestParam(required = false) String airline){
        try {
            RevenueReportModel annualReport = revenueReportService.generateAnnualRevenueReport(year, passengerClass, airline);
            Map map = new HashMap();
            map.put("data",annualReport);
            map.put("message", "sukses");
            map.put("status", 200);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}

