package com.backend.travelid.service.impl;

import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.oauth.User;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.repository.oauth.UserRepository;
import com.backend.travelid.service.CustomerService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TemplateResponse response;

    @Override
    public Map addCustomer(Customer customer) {
        try {
            log.info("add User");
            if (customer.getName() == null) {
                return response.Error(Config.NAME_REQUIRED);
            }
            if (!response.nameNotSimbol(customer.getName())) {
                return response.Error(Config.NAME_MUST_NOT_BE_SYMBOL);
            }
            if (customer.getIdentityNumber() == null) {
                return response.Error(Config.IDENTITY_NUMBER_REQUIRED);
            }
            if (customer.getEmail() == null) {
                return response.Error(Config.EMAIL_REQUIRED);
            }
            if (customer.getDateOfBirth() == null) {
                return response.Error(Config.DOB_REQUIRED);
            }
            return response.templateSaveSukses(customerRepository.save(customer));
        }catch (Exception e){
            log.error("add user error: "+e.getMessage());
            return response.Error("add user ="+e.getMessage());
        }
    }

    @Override
    public Map updateCustomer(Customer customer) {
        try {
            log.info("Update User");
            if (customer.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            if (customer.getEmail() == null) {
                return response.Error(Config.EMAIL_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(customer.getId());
            if (chekDataDBCustomer.isEmpty()) {
                return response.Error(Config.CUSTOMER_NOT_FOUND);
            }
            Optional<User> chekDataDBUser = userRepository.findByEmail(customer.getEmail());
            if (chekDataDBUser.isEmpty()) {
                return response.Error(Config.USER_NOT_FOUND);
            }

            chekDataDBCustomer.get().setName(customer.getName());
            chekDataDBCustomer.get().setIdentityNumber(customer.getIdentityNumber());
            chekDataDBCustomer.get().setDateOfBirth(customer.getDateOfBirth());
            chekDataDBCustomer.get().setGender(customer.getGender());
            chekDataDBCustomer.get().setUpdated_date(new Date());

            chekDataDBUser.get().setFullname(customer.getName());

            User objUser = userRepository.save(chekDataDBUser.get());
            Customer objCustomer = customerRepository.save(chekDataDBCustomer.get());

            return response.templateSukses(objUser, objCustomer);
        }catch (Exception e){
            log.error("Update User error: "+e.getMessage());
            return response.Error("Update User="+e.getMessage());
        }
    }

    @Override
    public Map deleteCustomer(Customer customer) {
        try {
            log.info("Delete user");
            if (customer.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<Customer> chekDataDBUser = customerRepository.findById(customer.getId());
            if (chekDataDBUser.isEmpty()) {
                return response.Error(Config.USER_NOT_FOUND);
            }

            chekDataDBUser.get().setDeleted_date(new Date());
            customerRepository.save(chekDataDBUser.get());
            return response.sukses(Config.SUCCESS);
        }catch (Exception e){
            log.error("Delete User error: "+e.getMessage());
            return response.Error("Delete User ="+e.getMessage());
        }
    }

    @Override
    public Map getByID(Long user) {
        Optional<Customer> getBaseOptional = customerRepository.findById(user);
        if(getBaseOptional.isEmpty()){
            return response.notFound(getBaseOptional);
        }
        return response.templateSukses(getBaseOptional);
    }
}

