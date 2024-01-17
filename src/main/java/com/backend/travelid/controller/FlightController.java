package com.backend.travelid.controller;

import com.backend.travelid.entity.Flight;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.service.FlightService;
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

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @PostMapping(value ={"/save","/save/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> saveFlight(@RequestBody Flight flight) {
        try {
            return new ResponseEntity<Map>(flightService.saveFlight(flight), HttpStatus.OK);
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
            @RequestParam(required = false) String airline,
            @RequestParam(required = false) String originCity,
            @RequestParam(required = false) String destinationCity,
            @RequestParam(required = false) String transit,
            @RequestParam(required = false) Boolean isDiscount,
            @RequestParam(required = false) Boolean freeMeal,
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
                        if (airline != null && !airline.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("airline")), "%" + airline.toLowerCase() + "%"));
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
                        if (isDiscount != null) {
                            predicates.add(criteriaBuilder.isTrue(root.get("isDiscount")));
                        }
                        if (freeMeal != null) {
                            predicates.add(criteriaBuilder.isTrue(root.get("freeMeal")));
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
