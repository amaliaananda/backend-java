package com.backend.travelid.controller;

import com.backend.travelid.dto.BookingRequestDTO;
import com.backend.travelid.dto.BookingRoundtripRequestDTO;
import com.backend.travelid.dto.PaymentRequestDTO;
import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.Customer;
import com.backend.travelid.repository.BookingRepository;
import com.backend.travelid.service.BookingService;
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
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    SimpleStringUtils simpleStringUtils = new SimpleStringUtils();

    @Autowired
    public BookingRepository bookingRepository;

    @Autowired
    public TemplateResponse response;

    @PostMapping(value = {"/save","/save/"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map> addBooking(@RequestBody Booking booking){
        try {
            return new ResponseEntity<Map>(bookingService.saveBooking(booking), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @PostMapping(value = {"/saveWithDetails","/saveWithDetails/"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map> saveBookingWithDetails(@RequestBody BookingRequestDTO bookingRequestDTO) {
        try {
            return new ResponseEntity<Map>(bookingService.saveBookingWithDetails(bookingRequestDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @PostMapping(value = {"/saveRoundtripWithDetails","/saveRoundtripWithDetails/"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map> saveRoundtripBookingWithDetails(@RequestBody BookingRoundtripRequestDTO bookingRoundtripRequestDTO) {
        try {
            return new ResponseEntity<Map>(bookingService.saveRoundtripBookingWithDetails(bookingRoundtripRequestDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @PutMapping(value = {"/processPayment","/processPayment/"})
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map> processPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            return new ResponseEntity<Map>(bookingService.processPayment(paymentRequestDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @PutMapping(value = {"/update","/update/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> updateBooking(@RequestBody Booking booking) {
        try {
            return new ResponseEntity<Map>(bookingService.updateBooking(booking), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @DeleteMapping(value = {"/delete","/delete/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> deleteBooking(@RequestBody Booking booking) {
        try {
            return new ResponseEntity<Map>(bookingService.deleteBooking(booking), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @GetMapping(value={"/{id}", "/{id}/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> getById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<Map>(bookingService.getByID(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @GetMapping(value = {"/bookingsByCustomerId","/bookingsByCustomerId/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> getBookingsByCustomerId(@RequestParam Long customerId) {
        try {
            Map list =bookingService.getByCustomerId(customerId);
            Map map = new HashMap();
            map.put("data",list);
            map.put("message", "sukses");
            map.put("status", 200);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping(value = {"/incomeByMonthAndYear/{year}", "/incomeByMonthAndYear/{year}/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> getIncomeByMonthAndYear(@PathVariable int year) {
        try {
            List<Map<String, Object>> incomeData = bookingService.getIncomeByMonthAndYear(year);
            Map map = new HashMap();
            map.put("data",incomeData);
            map.put("message", "sukses");
            map.put("status", 200);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping(value = {"/listBookings", "/listBookings/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> list(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = false) String paid,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype) {
        try {
            Pageable show_data = simpleStringUtils.getShort(orderby, ordertype, page, size);

            Specification<Booking> spec =
                    ((root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        if (paid != null && !paid.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("paid")), "%" + paid.toLowerCase() + "%"));
                        }
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    });
            Page<Booking> list = bookingRepository.findAll(spec, show_data);

            Map map = new HashMap();
            map.put("data",list);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}

