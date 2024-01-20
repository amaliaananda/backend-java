package com.backend.travelid.controller;

import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.Notification;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.repository.NotificationRepository;
import com.backend.travelid.service.NotificationService;
import com.backend.travelid.utils.Config;
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
import java.util.*;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    SimpleStringUtils simpleStringUtils = new SimpleStringUtils();

    @Autowired
    public NotificationRepository notificationRepository;

    @Autowired
    public CustomerRepository customerRepository;

    @Autowired
    public TemplateResponse response;

    @GetMapping(value={"/{id}", "/{id}/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> getById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<Map>(notificationService.getByID(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @GetMapping(value = {"/notificationsByCustomerId/{customerId}","/notificationsByCustomerId/{customerId}/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> getBookingsByCustomerId(@PathVariable("customerId") Long customerId) {
        try {
            return new ResponseEntity<Map>(notificationService.getByCustomerId(customerId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping(value = {"/listNotifications", "/listNotifications/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> list(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype) {
        try {
            Pageable show_data = simpleStringUtils.getShort(orderby, ordertype, page, size);

            Specification<Notification> spec =
                    ((root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        if (customerId != null && !customerId.isEmpty()) {
                            long custId = Long.parseLong(customerId);
                            Optional<Customer> chekDataDBCustomer = customerRepository.findById(custId);
                            if (chekDataDBCustomer.isEmpty()) {
                                throw new RuntimeException(Config.USER_NOT_FOUND);
                            }
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("customerId")), "%" + customerId + "%"));
                        }
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    });
            Page<Notification> list = notificationRepository.findAll(spec, show_data);

            Map map = new HashMap();
            map.put("data",list);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}

