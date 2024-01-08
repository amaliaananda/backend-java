package com.backend.travelid.utils;

import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Data
public class Config {
    String code = "status", message = "message";
    public String code_notFound ="404";

    public String codeRequired ="403";
    public String isRequired =" is Required";

    public String code_sukses = "200";
    public String code_server = "500";
    public String code_null = "1";
    public String message_sukses = "sukses";
    public static Integer  EROR_CODE_404 =404;

    public static Integer  ERROR_403 =403;

    public static Integer  ERROR_500 =500;

    public  static  String NAME_REQUIRED = "Name is Required.";

    public  static  String IDENTITY_NUMBER_REQUIRED = "Identity Number is Required.";

    public  static  String AIRLINE_REQUIRED = "Airline is Required.";

    public  static  String AIRPORT_REQUIRED = "Airport is Required.";

    public  static  String ID_REQUIRED = "Id is Required.";

    public  static  String USERNAME_REQUIRED = "Username is Required.";

    public  static  String FLIGHT_NAME_REQUIRED = "FLIGHT name is Required.";

    public  static  String FLIGHT_REQUIRED = "FLIGHT is Required.";

    public  static  String FLIGHT_NOT_FOUND = "FLIGHT not found.";

    public  static  String TICKET_REQUIRED = "TICKET is Required.";

    public  static  String TICKET_NOT_FOUND = "TICKET not found.";

    public  static  String BOOKING_REQUIRED = "BOOKING is Required.";

    public  static  String BOOKING_NOT_FOUND = "BOOKING not found.";

    public  static  String BOOKING_DETAIL_REQUIRED = "BOOKING DETAIL is Required.";

    public  static  String BOOKING_DETAIL_NOT_FOUND = "BOOKING DETAIL not found.";

    public  static  String CUSTOMER_REQUIRED = "CUSTOMER is Required.";

    public  static  String CUSTOMER_NOT_FOUND = "CUSTOMER not found.";

    public  static  String USER_REQUIRED = "USER is Required.";

    public  static  String USER_NOT_FOUND = "USER not found.";

    public  static  String SUCCESS = "Success.";

    public String convertDateToString(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String strDate = dateFormat.format(date);
        return strDate;
    }
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }
}

