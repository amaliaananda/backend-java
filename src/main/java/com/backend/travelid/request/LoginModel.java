package com.backend.travelid.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginModel {
    private String username;
    private String password;
}

