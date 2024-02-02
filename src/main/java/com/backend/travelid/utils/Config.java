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

    public String code_sukses = "201";
    public String code_server = "500";
    public String code_null = "1";
    public String message_sukses = "sukses";
    public static Integer  EROR_CODE_404 =404;

    public static Integer  ERROR_403 =403;

    public static Integer  ERROR_500 =500;

    public  static  String NAME_REQUIRED = "Name is Required.";

    public  static  String IDENTITY_NUMBER_ALREADY_USED = "identity number already usud.";

    public  static  String NAME_MUST_NOT_BE_SYMBOL = "Name must not be symbol.";

    public  static  String IDENTITY_NUMBER_REQUIRED = "Identity Number is Required.";

    public  static  String LIST_BOOKING_DETAIL_REQUIRED = "List Booking Detail is Required.";

    public  static  String LIST_OUTBOUND_BOOKING_DETAIL_REQUIRED = "List OUTBOUND Booking Detail is Required.";

    public  static  String LIST_RETURN_BOOKING_DETAIL_REQUIRED = "List RETURN Booking Detail is Required.";

    public  static  String AIRLINE_REQUIRED = "Airline is Required.";

    public  static  String AIRLINE_NOT_FOUND = "Airline NOT FOUND.";

    public  static  String ORIGIN_AIRPORT_REQUIRED = "Origin Airport is Required.";

    public  static  String DESTINATION_AIRPORT_REQUIRED = "Destination Airport is Required.";

    public  static  String ORIGIN_CITY_REQUIRED = "Origin city is Required.";

    public  static  String DESTINATION_CITY_REQUIRED = "Destination city is Required.";

    public  static  String FLIGHT_NUMBER_REQUIRED = "flight number is Required.";

    public  static  String GATE_REQUIRED = "gate is Required.";

    public  static  String FLIGHT_TIME_REQUIRED = "FLIGHT TIME is Required.";

    public  static  String ARRIVED_TIME_REQUIRED = "ARRIVED TIME is Required.";

    public  static  String DURATION_REQUIRED = "DURATION is Required.";

    public  static  String TRANSIT_REQUIRED = "TRANSIT is Required.";

    public  static  String LUGGAGE_REQUIRED = "LUGGAGE is Required.";

    public  static  String FREEMEAL_REQUIRED = "FREEMEAL is Required.";

    public  static  String PASSENGER_CLASS_REQUIRED = "passenger class is Required.";

    public  static  String PASSENGER_CLASS_NOT_FOUND = "passenger class not found.";

    public  static  String PRICE_REQUIRED = "Price is Required.";

    public  static  String EMAIL_REQUIRED = "EMAIL is Required.";

    public  static  String DOB_REQUIRED = "date of birth is Required.";

    public  static  String TOTAL_PRICE_REQUIRED = "total price is Required.";

    public  static  String PAID_REQUIRED = "paid is Required.";

    public  static  String SEAT_NUMBER_REQUIRED = "seat number is Required.";

    public  static  String ID_REQUIRED = "Id is Required.";

    public  static  String USERNAME_REQUIRED = "Username is Required.";

    public  static  String FLIGHT_NAME_REQUIRED = "FLIGHT name is Required.";

    public  static  String FLIGHT_REQUIRED = "FLIGHT is Required.";

    public  static  String FLIGHT_NOT_FOUND = "FLIGHT not found.";

    public  static  String SEAT_REQUIRED = "SEAT is Required.";

    public  static  String SEAT_NOT_FOUND = "SEAT not found.";

    public  static  String TICKET_REQUIRED = "TICKET is Required.";

    public  static  String TICKET_NOT_FOUND = "TICKET not found.";

    public  static  String BOOKING_REQUIRED = "BOOKING is Required.";

    public  static  String BOOKING_NOT_FOUND = "BOOKING not found.";

    public  static  String THIS_CUSTOMER_NOT_DOING_BOOKING_YET = "THIS CUSTOMER NOT DOING BOOKING YET.";

    public  static  String BOOKING_DETAIL_REQUIRED = "BOOKING DETAIL is Required.";

    public  static  String BOOKING_DETAIL_NOT_FOUND = "BOOKING DETAIL not found.";

    public  static  String CUSTOMER_REQUIRED = "CUSTOMER is Required.";

    public  static  String CUSTOMER_NOT_FOUND = "CUSTOMER not found.";

    public  static  String USER_REQUIRED = "USER is Required.";

    public  static  String USER_NOT_FOUND = "USER not found.";

    public  static  String EMAIL_NOT_VALID = "email not valid.";

    public  static  String PASSWORD_NOT_VALID = "password not valid.";

    public  static  String SEAT_ALREADY_BOOKED = "seat already booked.";

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

    public boolean isValidPassword(String password) {
        // Definisikan pola regex untuk password
        // Contoh: minimal 8 karakter, minimal satu huruf besar, satu huruf kecil, dan satu angka
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

        // Buat objek Pattern
        Pattern pat = Pattern.compile(passwordRegex);

        // Lakukan pencocokan dengan password yang diberikan
        return pat.matcher(password).matches();
    }
}

