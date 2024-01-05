package com.backend.travelid.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
@Data
public class RegisterUserModel {
    @NotEmpty(message = "username is required.")
    private String username;

    @NotEmpty(message = "password is required.")
    private String password;

    @NotEmpty(message = "fullname is required.")
    private String fullname;

    @NotEmpty(message = "identity Number is required.")
    private String identityNumber;

    private String dateOfBirth;

    private String gender;
}


