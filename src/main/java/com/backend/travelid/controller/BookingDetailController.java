package com.backend.travelid.controller;

import com.backend.travelid.entity.BookingDetail;
import com.backend.travelid.repository.BookingDetailRepository;
import com.backend.travelid.service.BookingDetailService;
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
@RequestMapping("/bookingDetail")
public class BookingDetailController {

    @Autowired
    private BookingDetailService bookingDetailService;

    SimpleStringUtils simpleStringUtils = new SimpleStringUtils();

    @Autowired
    public BookingDetailRepository bookingDetailRepository;

    @Autowired
    public TemplateResponse response;

    @PostMapping(value = {"/save","/save/"})
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map> addBookingDetail(@RequestBody BookingDetail bookingDetail){
        try {
            return new ResponseEntity<Map>(bookingDetailService.saveBookingDetail(bookingDetail), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @PutMapping(value = {"/update","/update/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> updateBookingDetail(@RequestBody BookingDetail bookingDetail) {
        try {
            return new ResponseEntity<Map>(bookingDetailService.updateBookingDetail(bookingDetail), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @DeleteMapping(value = {"/delete","/delete/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> deleteBookingDetail(@RequestBody BookingDetail bookingDetail) {
        try {
            return new ResponseEntity<Map>(bookingDetailService.deleteBookingDetail(bookingDetail), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @GetMapping(value={"/{id}", "/{id}/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> getById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<Map>(bookingDetailService.getByID(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @GetMapping(value = {"/getByCustomerName","/bookingsByCustomerName/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> getByCustomerName(@RequestParam String customerName) {
        try {
            return new ResponseEntity<Map>(bookingDetailService.getByCustomerName(customerName), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping(value = {"/listBookingDetails", "/listBookingDetails/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> list(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String identityNumber,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype) {
        try {
            Pageable show_data = simpleStringUtils.getShort(orderby, ordertype, page, size);

            Specification<BookingDetail> spec =
                    ((root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        if (customerName != null && !customerName.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("customerName")), "%" + customerName.toLowerCase() + "%"));
                        }
                        if (identityNumber != null && !identityNumber.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("identityNumber")), "%" + identityNumber.toLowerCase() + "%"));
                        }
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    });
            Page<BookingDetail> list = bookingDetailRepository.findAll(spec, show_data);

            Map map = new HashMap();
            map.put("data",list);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}

