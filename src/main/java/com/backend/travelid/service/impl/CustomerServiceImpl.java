package com.backend.travelid.service.impl;

import com.backend.travelid.entity.Customer;
import com.backend.travelid.repository.CustomerRepository;
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
    private TemplateResponse response;

    @Override
    public Map addCustomer(Customer customer) {
        try {
            log.info("add User");
            return response.sukses(customerRepository.save(customer));
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
            Optional<Customer> chekDataDBUser = customerRepository.findById(customer.getId());
            if (chekDataDBUser.isEmpty()) {
                return response.Error(Config.USER_NOT_FOUND);
            }
            chekDataDBUser.get().setName(customer.getName());
            chekDataDBUser.get().setIdentityNumber(customer.getIdentityNumber());
            chekDataDBUser.get().setEmail(customer.getEmail());
            chekDataDBUser.get().setDateOfBirth(customer.getDateOfBirth());
            chekDataDBUser.get().setGender(customer.getGender());
            chekDataDBUser.get().setUpdated_date(new Date());

            return response.sukses(customerRepository.save(chekDataDBUser.get()));
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

