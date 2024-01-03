package com.backend.travelid.controller.oauth;


import com.backend.travelid.entity.oauth.User;
import com.backend.travelid.repository.oauth.UserRepository;
import com.backend.travelid.request.ResetPasswordModel;
import com.backend.travelid.service.email.EmailSender;
import com.backend.travelid.service.oauth.UserService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.EmailTemplate;
import com.backend.travelid.utils.SimpleStringUtils;
import com.backend.travelid.utils.TemplateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/v1/forget-password/")
public class ForgetPasswordController {

    @Autowired
    private UserRepository userRepository;

    Config config = new Config();

    @Autowired
    public UserService serviceReq;

    @Value("${expired.token.password.minute:}")//FILE_SHOW_RUL
    private int expiredToken;

    @Autowired
    public TemplateResponse templateCRUD;

    @Autowired
    public EmailTemplate emailTemplate;

    @Autowired
    public EmailSender emailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Step 1 : Send OTP
    @PostMapping("/send")//send OTP//send OTP
    public Map sendEmailPassword(@RequestBody ResetPasswordModel user) {
        String message = "Thanks, please check your email";

        if (StringUtils.isEmpty(user.getEmail())) return templateCRUD.templateEror("No email provided");
        User found = userRepository.findOneByUsername(user.getEmail());
        if (found == null) return templateCRUD.notFound("Email not found"); //throw new BadRequest("Email not found");

        String template = emailTemplate.getResetPassword();
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
            found.setOtpExpiredDate(expirationDate);
            template = template.replaceAll("\\{\\{PASS_TOKEN}}", otp);
            template = template.replaceAll("\\{\\{USERNAME}}", (found.getUsername() == null ? "" +
                    "@UserName"
                    :
                    "@" + found.getUsername()));

            userRepository.save(found);
        } else {
            template = template.replaceAll("\\{\\{USERNAME}}", (found.getUsername() == null ? "" +
                    "@UserName"
                    :
                    "@" + found.getUsername()));
            template = template.replaceAll("\\{\\{PASS_TOKEN}}", found.getOtp());
        }
        emailSender.sendAsync(found.getUsername(), "Chute - Forget Password", template);


        return templateCRUD.templateSukses("success");

    }

    //Step 2 : CHek TOKEN OTP EMAIL
    @PostMapping("/validate")
    public Map cheKTOkenValid(@RequestBody ResetPasswordModel model) {
        if (model.getOtp() == null) return templateCRUD.notFound("Token is required");

        User user = userRepository.findOneByOTP(model.getOtp());
        if (user == null) {
            return templateCRUD.templateEror("Token not valid");
        }

        return templateCRUD.templateSukses("Success");
    }

    // Step 3 : lakukan reset password baru
    @PostMapping("/change-password")
    public Map resetPassword(@RequestBody ResetPasswordModel model) {
        if (model.getOtp() == null) return templateCRUD.notFound("Token is required");
        if (model.getNewPassword() == null) return templateCRUD.notFound("New Password is required");
        User user = userRepository.findOneByOTP(model.getOtp());
        String success;
        if (user == null) return templateCRUD.notFound("Token not valid");

        user.setPassword(passwordEncoder.encode(model.getNewPassword().replaceAll("\\s+", "")));
        user.setOtpExpiredDate(null);
        user.setOtp(null);

        try {
            userRepository.save(user);
            success = "success";
        } catch (Exception e) {
            return templateCRUD.templateEror("Gagal simpan user");
        }
        return templateCRUD.templateSukses(success);
    }


}

