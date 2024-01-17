package com.backend.travelid.service;

import com.backend.travelid.entity.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CustomerService {
    Map addCustomer(Customer customer);
    Map updateCustomer(Customer customer);
    Map updateProfilePicture(MultipartFile profilePictureFile, Long IdCustomer);
    Map deleteCustomer(Customer customer);
    Map getByID(Long user);
}

