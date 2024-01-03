package com.backend.travelid.service;

import com.backend.travelid.entity.Customer;

import java.util.Map;

public interface CustomerService {
    Map addCustomer(Customer customer);
    Map updateCustomer(Customer customer);
    Map deleteCustomer(Customer customer);
    Map getByID(Long user);
}

