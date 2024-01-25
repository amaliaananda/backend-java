package com.backend.travelid.controller.oauth;

import com.backend.travelid.repository.oauth.UserRepository;
import com.backend.travelid.service.oauth.UserService;
import com.backend.travelid.service.oauth.Oauth2UserDetailsService;
import com.backend.travelid.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    public TemplateResponse response;

    @GetMapping("/detail-profile")
    public ResponseEntity<Map> detailProfile(Principal principal) {
        try {
            Map map = userService.getDetailProfile(principal);
            return new ResponseEntity<Map>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<Map>(response.Error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

}
