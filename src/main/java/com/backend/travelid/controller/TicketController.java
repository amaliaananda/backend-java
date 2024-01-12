package com.backend.travelid.controller;

import com.backend.travelid.entity.Ticket;
import com.backend.travelid.repository.TicketRepository;
import com.backend.travelid.service.TicketService;
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
@RequestMapping("/ticket")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    SimpleStringUtils simpleStringUtils = new SimpleStringUtils();

    @Autowired
    public TicketRepository ticketRepository;

    @Autowired
    public TemplateResponse response;

    @PostMapping(value = {"/save","/save/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> addTicket(@RequestBody Ticket ticket){
        try {
            return new ResponseEntity<Map>(ticketService.saveTicket(ticket), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @PutMapping(value = {"/update","/update/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> updateTicket(@RequestBody Ticket ticket) {
        try {
            return new ResponseEntity<Map>(ticketService.updateTicket(ticket), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @DeleteMapping(value = {"/delete","/delete/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> deleteTicket(@RequestBody Ticket ticket) {
        try {
            return new ResponseEntity<Map>(ticketService.deleteTicket(ticket), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @GetMapping(value={"/{id}", "/{id}/"})
    public ResponseEntity<Map> getById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<Map>(ticketService.getByID(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping(value = {"/listTickets", "/listTickets/"})
    public ResponseEntity<Map> list(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = false) String passengerClass,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype) {
        try {
            Pageable show_data = simpleStringUtils.getShort(orderby, ordertype, page, size);

            Specification<Ticket> spec =
                    ((root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        if (passengerClass != null && !passengerClass.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("passengerClass")), "%" + passengerClass.toLowerCase() + "%"));
                        }
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    });
            Page<Ticket> list = ticketRepository.findAll(spec, show_data);

            Map map = new HashMap();
            map.put("data",list);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}

