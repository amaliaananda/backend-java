package com.backend.travelid.controller.oauth;

import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.oauth.User;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.repository.oauth.UserRepository;
import com.backend.travelid.request.RegisterUserModel;
import com.backend.travelid.service.email.EmailSender;
import com.backend.travelid.service.oauth.UserService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.EmailTemplate;
import com.backend.travelid.utils.SimpleStringUtils;
import com.backend.travelid.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/user-register/")
public class RegisterUserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    Config config = new Config();

    @Autowired
    public UserService serviceReq;

    @Autowired
    public EmailSender emailSender;
    @Autowired
    public EmailTemplate emailTemplate;

    @Value("${expired.token.password.minute:}")//FILE_SHOW_RUL
    private int expiredToken;

    @Autowired
    public TemplateResponse templateCRUD;

    @PostMapping("/register")
    public ResponseEntity<Map> saveRegisterManual(@Valid @RequestBody RegisterUserModel objModel) throws RuntimeException {
        Map map = new HashMap();

        if (!config.isValidEmail(objModel.getUsername().toLowerCase())){
            return new ResponseEntity<Map>(templateCRUD.Error("email not valid"), HttpStatus.NOT_FOUND);
        }
        if (!config.isValidPassword(objModel.getPassword())){
            return new ResponseEntity<Map>(templateCRUD.Error("password not valid"), HttpStatus.NOT_FOUND);
        }

        User user = userRepository.checkExistingEmail(objModel.getUsername());
        if (null != user) {
            return new ResponseEntity<Map>(templateCRUD.Error("Username already used"), HttpStatus.NOT_FOUND);
        }
        Customer customers = customerRepository.checkExistingIdentityNumber(objModel.getIdentityNumber());
        if (null != customers) {
            return new ResponseEntity<Map>(templateCRUD.Error("Identity Number already used"), HttpStatus.NOT_FOUND);
        }
        map = serviceReq.registerManual(objModel);
        //gunanya send email : otomatis send email
        Map mapRegister = sendEmailegister(objModel);
        return new ResponseEntity<Map>(mapRegister, HttpStatus.OK);
    }


    @PostMapping("/register-google") // Step 1
    public ResponseEntity<Map> saveRegisterByGoogle(@Valid @RequestBody RegisterUserModel objModel) throws RuntimeException {
        Map map = new HashMap();

        User user = userRepository.checkExistingEmail(objModel.getUsername());
        if (null != user) {
            return new ResponseEntity<Map>(templateCRUD.Error("Username sudah ada"), HttpStatus.OK);

        }
        map = serviceReq.registerByGoogle(objModel);
        //gunanya send email : otomatis send email
        Map mapRegister = sendEmailegister(objModel);
        return new ResponseEntity<Map>(mapRegister, HttpStatus.OK);

    }


    // Step 2: sendp OTP berupa URL: guna updeta enable agar bisa login:
    @PostMapping("/send-otp")//send OTP
    public Map sendEmailegister(
            @RequestBody RegisterUserModel user) {
        String message = "Thanks, please check your email for activation.";

        if (user.getUsername() == null) return templateCRUD.Error("No email provided");
        User found = userRepository.findOneByUsername(user.getUsername());
        if (found == null) return templateCRUD.Error("Email not found"); //throw new BadRequest("Email not found");

        String template = emailTemplate.getRegisterTemplate();
        if (StringUtils.isEmpty(found.getOtp())) {
            User search;
            String otp;
            do {
                otp = SimpleStringUtils.randomString(6, true);
                search = userRepository.findOneByOTP(otp);
            } while (search != null);
            Date dateNow = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateNow);
            calendar.add(Calendar.MINUTE, expiredToken);
            Date expirationDate = calendar.getTime();

            found.setOtp(otp);
            found.setOtpExpiredDate(expirationDate); //akan menjadi validasi
            template = template.replaceAll("\\{\\{USERNAME}}", (found.getFullname() == null ? found.getUsername() : found.getFullname()));
            template = template.replaceAll("\\{\\{VERIFY_TOKEN}}", otp);
            userRepository.save(found);
        } else {
            template = template.replaceAll("\\{\\{USERNAME}}", (found.getFullname() == null ? found.getUsername() : found.getFullname()));
            template = template.replaceAll("\\{\\{VERIFY_TOKEN}}", found.getOtp());
        }
        emailSender.sendAsync(found.getUsername(), "Register", template);
        return templateCRUD.Sukses(message);
    }

    //step ke 3
    @GetMapping("/register-confirm-otp/{token}")
    public ResponseEntity<Map> saveRegisterManual(@PathVariable(value = "token") String tokenOtp) throws RuntimeException {

        User user = userRepository.findOneByOTP(tokenOtp);
        if (null == user) {
            return new ResponseEntity<Map>(templateCRUD.templateEror("OTP tidak ditemukan"), HttpStatus.OK);
        }
//validasi jika sebelumnya sudah melakukan aktifasi

        if (user.isEnabled()) {
            return new ResponseEntity<Map>(templateCRUD.templateSukses("Akun Anda sudah aktif, Silahkan melakukan login"), HttpStatus.OK);
        }
        String today = config.convertDateToString(new Date());

        String dateToken = config.convertDateToString(user.getOtpExpiredDate());
        if (Long.parseLong(today) > Long.parseLong(dateToken)) {
            return new ResponseEntity<Map>(templateCRUD.templateEror("Your token is expired. Please Get token again."), HttpStatus.OK);
        }
        //update user : ini yang berubah
        user.setEnabled(true);
        userRepository.save(user);

        return new ResponseEntity<Map>(templateCRUD.templateSukses("Sukses, Silahkan Melakukan Login"), HttpStatus.OK);
    }


}

