package com.backend.travelid.controller;

import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.Seat;
import com.backend.travelid.repository.SeatRepository;
import com.backend.travelid.service.SeatService;
import com.backend.travelid.utils.SimpleStringUtils;
import com.backend.travelid.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seat")
public class SeatController {

    @Autowired
    private SeatService seatService;

    SimpleStringUtils simpleStringUtils = new SimpleStringUtils();

    @Autowired
    public SeatRepository seatRepository;

    @Autowired
    public TemplateResponse response;
    @GetMapping(value={"/{id}", "/{id}/"})
    public ResponseEntity<Map> getById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<Map>(seatService.getByID(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping(value={"/getByFlight/{id}", "/getByFlight/{id}/"})
    public ResponseEntity<Map>getByFlight(@PathVariable("id") Long id) {
        try {
            List<Seat> list = seatService.getByFlight(id);
            Map map = new HashMap();
            map.put("data",list);
            map.put("message", "sukses");
            map.put("status", 200);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}

