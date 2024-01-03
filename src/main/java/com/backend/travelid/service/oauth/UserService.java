package com.backend.travelid.service.oauth;


import com.backend.travelid.request.LoginModel;
import com.backend.travelid.request.RegisterUserModel;

import java.util.Map;

public interface UserService {
    Map registerManual(RegisterUserModel objModel) ;

    Map registerByGoogle(RegisterUserModel objModel) ;

    public Map login(LoginModel objLogin);
}




