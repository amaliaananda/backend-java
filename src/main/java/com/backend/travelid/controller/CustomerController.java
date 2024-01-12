package com.backend.travelid.controller;

import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.oauth.User;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.service.CustomerService;
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
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    SimpleStringUtils simpleStringUtils = new SimpleStringUtils();

    @Autowired
    public CustomerRepository customerRepository;

    @Autowired
    public TemplateResponse response;

    @PostMapping(value ={"/add","/add/"})
    public ResponseEntity<Map> addCustomer(@Valid @RequestBody Customer customer) {
        try {
            Customer customers = customerRepository.checkExistingIdentityNumber(customer.getIdentityNumber());
            if (null != customers) {
                return new ResponseEntity<Map>(response.Error("Identity Number already used"), HttpStatus.OK);
            }
            return new ResponseEntity<Map>(customerService.addCustomer(customer), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @PutMapping(value={"/update", "/update/"})
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<Map> updateCustomer(@RequestBody Customer customer) {
        try {
            Customer customers = customerRepository.checkExistingIdentityNumber(customer.getIdentityNumber());
            if (null != customers) {
                return new ResponseEntity<Map>(response.Error("Identity Number already used"), HttpStatus.OK);
            }
            return new ResponseEntity<Map>(customerService.updateCustomer(customer), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    @DeleteMapping(value={"/delete", "/delete/"})
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<Map> deleteCustomer(@RequestBody Customer customer) {
        try {
            return new ResponseEntity<Map>(customerService.deleteCustomer(customer), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping(value={"/{id}", "/{id}/"})
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<Map> getById(@PathVariable("id") Long id) {
        try {
            return new ResponseEntity<Map>(customerService.getByID(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
    @GetMapping(value = {"/listCustomers", "/listCustomers/"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map> list(
            @RequestParam() Integer page,
            @RequestParam(required = true) Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String orderby,
            @RequestParam(required = false) String ordertype) {
        try {
            Pageable show_data = simpleStringUtils.getShort(orderby, ordertype, page, size);

            Specification<Customer> spec =
                    ((root, query, criteriaBuilder) -> {
                        List<Predicate> predicates = new ArrayList<>();
                        if (name != null && !name.isEmpty()) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
                        }
                        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
                    });
            Page<Customer> list = customerRepository.findAll(spec, show_data);

            Map map = new HashMap();
            map.put("data",list);
            return new ResponseEntity<Map>(map, new HttpHeaders(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}

