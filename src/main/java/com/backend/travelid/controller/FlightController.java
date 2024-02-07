package com.backend.travelid.controller;

import com.backend.travelid.entity.Airlines;
import com.backend.travelid.entity.Flight;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.service.FlightService;
import com.backend.travelid.utils.SimpleStringUtils;
import com.backend.travelid.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

@RestController
@RequestMapping("/flight")
public class FlightController {

    @Autowired
    private FlightService flightService;
    SimpleStringUtils simpleStringUtils = new SimpleStringUtils();

    @Autowired
    public FlightRepository flightRepository;

    @Autowired
    public TemplateResponse response;

    @PostMapping(value ={"/save/{airlineId}","/save/{airlineId}/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> saveFlight(@RequestBody Flight flight, @PathVariable("airlineId") Long airlineId) {
        try {
            return new ResponseEntity<Map>(flightService.saveFlight(flight, airlineId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @PutMapping(value={"/update", "/update/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> updateFlight(@RequestBody Flight flight) {
        try {
            return new ResponseEntity<Map>(flightService.updateFlight(flight), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @DeleteMapping(value={"/delete", "/delete/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> deleteFlight(@RequestBody Flight flight) {
        try {
            return new ResponseEntity<Map>(flightService.deleteFlight(flight), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping(value={"/{id}", "/{id}/"})
    public ResponseEntity<Map> getById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<Map>(flightService.getByID(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @GetMapping(value={"/flightsByAirline/{airline}", "/flightsByAirline/{airline}/"})
    public List<Flight> getFlightsByAirline(@PathVariable("airline") String airline) {
        return flightService.getFlightsByAirline(airline);
    }
    @GetMapping(value={"/flightsByPassengerClass/{passengerClass}", "/flightsByPassengerClass/{passengerClass}/"})
    public List<Flight> getFlightsByPassengerClass(@PathVariable("passengerClass") String passengerClass) {
        return flightService.getFlightsByPassengerClass(passengerClass);
    }

    @GetMapping(value = {"/listFlights", "/listFlights/"})
    public ResponseEntity<Map> list(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = false) String passengerClass,
            @RequestParam(required = false) String originAirport,
            @RequestParam(required = false) String destinationAirport,
            @RequestParam(required = false) String airlines,
            @RequestParam(required = false) String originCity,
            @RequestParam(required = false) String destinationCity,
            @RequestParam(required = false) String transit,
            @RequestParam(required = false) String endDateStr,
            @RequestParam(required = false) String startDateStr,
            @RequestParam(required = false) String isDiscount,
            @RequestParam(required = false) String freeMeal,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype) {
        try {
            Pageable show_data = simpleStringUtils.getShort(orderby, ordertype, page, size);

            Specification<Flight> spec =
                    ((root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        if (passengerClass != null && !passengerClass.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("passengerClass")), "%" + passengerClass.toLowerCase() + "%"));
                        }
                        if (originAirport != null && !originAirport.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("originAirport")), "%" + originAirport.toLowerCase() + "%"));
                        }
                        if (destinationAirport != null && !destinationAirport.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationAirport")), "%" + destinationAirport.toLowerCase() + "%"));
                        }
                        if (airlines != null && !airlines.isEmpty()) {
                            Join<Flight, Airlines> airlineJoin = root.join("airlines", JoinType.INNER); // Join dengan tabel airline

                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(airlineJoin.get("airline")), "%" + airlines.toLowerCase() + "%"));
                        }
                        if (originCity != null && !originCity.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("originCity")), "%" + originCity.toLowerCase() + "%"));
                        }
                        if (originCity != null && !originCity.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("originCity")), "%" + originCity.toLowerCase() + "%"));
                        }
                        if (destinationCity != null && !destinationCity.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("destinationCity")), "%" + destinationCity.toLowerCase() + "%"));
                        }
                        if (transit != null && !transit.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("transit")), "%" + transit.toLowerCase() + "%"));
                        }
                        if (startDateStr != null && endDateStr != null && !startDateStr.isEmpty() && !endDateStr.isEmpty()) {
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

                            LocalDateTime startDateTime = LocalDateTime.parse(startDateStr, formatter);
                            LocalDateTime endDateTime = LocalDateTime.parse(endDateStr, formatter);

                            Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
                            Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

                            predicates.add(criteriaBuilder.between(root.get("flightTime"), startDate, endDate));
                        }
                        if (isDiscount != null && !isDiscount.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("isDiscount")), "%" + isDiscount.toLowerCase() + "%"));
                        }
                        if (freeMeal != null && !freeMeal.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("freeMeal")), "%" + freeMeal.toLowerCase() + "%"));
                        }
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    });
            Page<Flight> list = flightRepository.findAll(spec, show_data);

            Map map = new HashMap();
            map.put("data",list);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}
