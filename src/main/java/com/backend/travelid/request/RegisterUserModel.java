package com.backend.travelid.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
@Data
public class RegisterUserModel {
    private String username;

    private String password;

    private String fullname;

    private String identityNumber;

    private String dateOfBirth;

    private String gender;

    private String PhoneNumber;
}


