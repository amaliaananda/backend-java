package com.backend.travelid.service.impl;

import com.backend.travelid.controller.fileupload.FileStorageService;
import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.oauth.User;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.repository.oauth.UserRepository;
import com.backend.travelid.service.CustomerService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
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
    private FileStorageService fileStorageService;

    @Autowired
    private TemplateResponse response;

    @Override
    public Map addCustomer(Customer customer) {
        try {
            log.info("add Customer");
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
            log.error("add Customer error: "+e.getMessage());
            return response.Error("add Customer ="+e.getMessage());
        }
    }

    @Override
    public Map updateCustomer(Customer customer) {
        try {
            log.info("Update Customer");
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
            chekDataDBCustomer.get().setDateOfBirth(customer.getDateOfBirth());
            chekDataDBCustomer.get().setGender(customer.getGender());
            chekDataDBCustomer.get().setPhoneNumber(customer.getPhoneNumber());
            chekDataDBCustomer.get().setUpdated_date(new Date());

            chekDataDBUser.get().setFullname(customer.getName());

            User objUser = userRepository.save(chekDataDBUser.get());
            Customer objCustomer = customerRepository.save(chekDataDBCustomer.get());

            return response.templateSukses(objUser, objCustomer);
        }catch (Exception e){
            log.error("Update Customer error: "+e.getMessage());
            return response.Error("Update Customer="+e.getMessage());
        }
    }
    @Override
    public Map updateProfilePicture(MultipartFile profilePictureFile, Long IdCustomer) {
        try {
            log.info("Update Profile Picture");
            if (IdCustomer == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(IdCustomer);
            if (chekDataDBCustomer.isEmpty()) {
                return response.Error(Config.CUSTOMER_NOT_FOUND);
            }

            // Update data customer
            Customer customerToUpdate = chekDataDBCustomer.get();
            customerToUpdate.setUpdated_date(new Date());

            // Handle profil picture
            if (profilePictureFile != null) {
                String profilePictureFileName = fileStorageService.storeFile(profilePictureFile);
                customerToUpdate.setProfilePicture(profilePictureFileName);
            }

            Customer updatedCustomer = customerRepository.save(customerToUpdate);

            return response.templateSukses(updatedCustomer);
        } catch (Exception e) {
            log.error("Update Profile Picture error: " + e.getMessage());
            return response.Error("Update Profile Picture=" + e.getMessage());
        }
    }

    @Override
    public Map deleteCustomer(Customer customer) {
        try {
            log.info("Delete Customer");
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
            log.error("Delete Customer error: "+e.getMessage());
            return response.Error("Delete Customer ="+e.getMessage());
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

    @Value("${app.uploadto.cdn}")//FILE_SHOW_RUL
    private String UPLOADED_FOLDER;

    public String uploadProfilePicture(MultipartFile profilePictureFile) {
        try {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("ddMyyyyhhmmss");
            String strDate = formatter.format(date);

            String fileExtension = StringUtils.getFilenameExtension(profilePictureFile.getOriginalFilename());
            String nameFormat = "." + (fileExtension.isEmpty() ? "png" : fileExtension);

            String fileName = UPLOADED_FOLDER + strDate + nameFormat;
            Path targetPath = Paths.get(fileName);

            Files.copy(profilePictureFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/v1/showFile/")
                    .path(targetPath.getFileName().toString())
                    .toUriString();

            return targetPath.getFileName().toString();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately, e.g., throw a custom exception
            return null;
        }
    }
}

