package com.backend.travelid.service.impl.oauth;

import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.oauth.Role;
import com.backend.travelid.entity.oauth.User;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.repository.oauth.RoleRepository;
import com.backend.travelid.repository.oauth.UserRepository;
import com.backend.travelid.request.LoginModel;
import com.backend.travelid.request.RegisterUserModel;
import com.backend.travelid.service.oauth.Oauth2UserDetailsService;
import com.backend.travelid.service.oauth.UserService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;

import java.security.Principal;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    Config config = new Config();
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    RoleRepository repoRole;

    @Autowired
    UserRepository repoUser;

    @Autowired
    CustomerRepository repoCustomer;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    public TemplateResponse response;

    @Value("${BASEURL}")
    private String baseUrl;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Oauth2UserDetailsService userDetailsService;

    @Override
    public Map registerManual(RegisterUserModel objModel) {
        Map map = new HashMap();
        try {
            String[] roleNames = {"ROLE_USER", "ROLE_READ", "ROLE_WRITE"}; // admin
            User user = new User();

            user.setUsername(objModel.getUsername().toLowerCase());
            user.setFullname(objModel.getFullname());

            Customer customer = new Customer();
            customer.setName(objModel.getFullname());
            customer.setEmail(objModel.getUsername());
            customer.setIdentityNumber(objModel.getIdentityNumber());
            customer.setDateOfBirth(objModel.getDateOfBirth());
            customer.setGender(objModel.getGender());
            customer.setPhoneNumber(objModel.getPhoneNumber());
            //step 1 :
            user.setEnabled(false); // matikan user

            String password = encoder.encode(objModel.getPassword().replaceAll("\\s+", ""));
            List<Role> r = repoRole.findByNameIn(roleNames);

            user.setRoles(r);
            user.setPassword(password);

            User objUser = repoUser.save(user);
            Customer objCustomer = repoCustomer.save(customer);

            return response.templateSaveSukses(objUser, objCustomer);

        } catch (Exception e) {
            logger.error("Error registerManual=", e);
            throw new RuntimeException("eror:"+e);
        }

    }
    @Override
    public Map registerByGoogle(RegisterUserModel objModel) {
        Map map = new HashMap();
        try {
            String[] roleNames = {"ROLE_USER", "ROLE_READ", "ROLE_WRITE"}; // ROLE DEFAULE
            User user = new User();
            user.setUsername(objModel.getUsername().toLowerCase());
            user.setFullname(objModel.getFullname());

            Customer customer = new Customer();
            customer.setName(objModel.getFullname());
            customer.setEmail(objModel.getUsername());
            customer.setIdentityNumber(objModel.getIdentityNumber());
            customer.setDateOfBirth(objModel.getDateOfBirth());
            customer.setGender(objModel.getGender());
            customer.setPhoneNumber(objModel.getPhoneNumber());
            //step 1 :
            user.setEnabled(false); // matikan user : tujuan kita inactifan
            String password = encoder.encode(objModel.getPassword().replaceAll("\\s+", ""));
            List<Role> r = repoRole.findByNameIn(roleNames);
            user.setRoles(r);
            user.setPassword(password);
            User obj = repoUser.save(user);
            Customer objCustomer = repoCustomer.save(customer);
            return response.templateSaveSukses(obj, objCustomer);

        } catch (Exception e) {
            logger.error("Eror registerManual=", e);
            return response.Error("eror:"+e);
        }
    }

    @Override
    public Map login(LoginModel loginModel) {
        /**
         * bussines logic for login here
         * **/
        try {
            Map<String, Object> map = new HashMap<>();

            User checkUser = repoUser.findOneByUsername(loginModel.getUsername());

            if ((checkUser != null) && (encoder.matches(loginModel.getPassword(), checkUser.getPassword()))) {
                if (!checkUser.isEnabled()) {
                    map.put("is_enabled", checkUser.isEnabled());
                    return response.Error("account_is_enabled");
                }
            }
            if (checkUser == null) {
                return response.notFound("user not found");
            }
            if (!(encoder.matches(loginModel.getPassword(), checkUser.getPassword()))) {
                return response.internalServer("wrong password");
            }
            String url = baseUrl + "/oauth/token?username=" + loginModel.getUsername() +
                    "&password=" + loginModel.getPassword() +
                    "&grant_type=password" +
                    "&client_id=my-client-web" +
                    "&client_secret=password";
            ResponseEntity<Map> responses = restTemplateBuilder.build().exchange(url, HttpMethod.POST, null, new
                    ParameterizedTypeReference<Map>() {
                    });

            if (responses.getStatusCode() == HttpStatus.OK) {
                User user = userRepository.findOneByUsername(loginModel.getUsername());
                List<String> roles = new ArrayList<>();

                for (Role role : user.getRoles()) {
                    roles.add(role.getName());
                }
                //save token
//                checkUser.setAccessToken(response.getBody().get("access_token").toString());
//                checkUser.setRefreshToken(response.getBody().get("refresh_token").toString());
//                userRepository.save(checkUser);

                map.put("access_token", responses.getBody().get("access_token"));
                map.put("token_type", responses.getBody().get("token_type"));
                map.put("refresh_token", responses.getBody().get("refresh_token"));
                map.put("expires_in", responses.getBody().get("expires_in"));
                map.put("scope", responses.getBody().get("scope"));
                map.put("jti", responses.getBody().get("jti"));

                return map;
            } else {
                return response.notFound("user not found");
            }
        } catch (HttpStatusCodeException e) {
            e.printStackTrace();
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("invalid login:"+e);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    @Override
    public Map getDetailProfile(Principal principal) {
        User idUser = getUserIdToken(principal, userDetailsService);
        Optional<Customer> idCustomer = getCustomerByEmail(idUser);
        try {
            User obj = userRepository.save(idUser);
            return response.templateSukses(obj, idCustomer);
        } catch (Exception e){
            return response.internalServer("500");
        }
    }

    private User getUserIdToken(Principal principal, Oauth2UserDetailsService userDetailsService) {
        UserDetails user = null;
        String username = principal.getName();
        if (!StringUtils.isEmpty(username)) {
            user = userDetailsService.loadUserByUsername(username);
        }

        if (null == user) {
            throw new InternalError("User not found");
        }
        User idUser = userRepository.findOneByUsername(user.getUsername());
        if (null == idUser) {
            throw new InternalError("User name not found");
        }
        return idUser;
    }
    private Optional<Customer> getCustomerByEmail(User idUser) {
        String email = idUser.getUsername();

        Optional<Customer> idCustomer = repoCustomer.findByEmail(email);
        if(idCustomer.isEmpty()){
            throw new InternalError("email not found");
        }
        return idCustomer;
    }
}