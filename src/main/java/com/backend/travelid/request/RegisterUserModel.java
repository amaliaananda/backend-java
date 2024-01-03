package com.backend.travelid.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
@Data
public class RegisterUserModel {
    @NotEmpty(message = "name is required.")
    private String name;

    @NotEmpty(message = "password is required.")
    private String password;

    @NotEmpty(message = "email is required.")
    private String email;

    @NotEmpty(message = "identity Number is required.")
    private String identityNumber;

    private String dateOfBirth;

    private String gender;
}


