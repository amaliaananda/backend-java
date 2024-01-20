package com.backend.travelid.service.impl;

import com.backend.travelid.entity.Booking;
import com.backend.travelid.repository.BookingRepository;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.service.NotificationService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.backend.travelid.entity.Notification;
import com.backend.travelid.entity.Customer;
import com.backend.travelid.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    public TemplateResponse response;

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public Map getByCustomerId(Long customerId) {
        try {
            log.info("get Notification by user");
            if (customerId == null) {
                throw new RuntimeException(Config.ID_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(customerId);
            if (chekDataDBCustomer.isEmpty()) {
                throw new RuntimeException(Config.USER_NOT_FOUND);
            }
            chekDataDBCustomer.get().setId(customerId);
            Optional<Notification> getBaseOptional = notificationRepository.getByCustomerId(customerId);
            if(getBaseOptional.isEmpty()){
                return response.notFound(getBaseOptional);
            }
            return response.templateSukses(getBaseOptional);
        }catch (Exception e){
            log.error("get Notification by Customer error: "+e.getMessage());
            throw new RuntimeException("get Notification by Customer ="+e.getMessage());
        }
    }
    @Override
    public Map getByID(Long notificationId) {
        Optional<Notification> getBaseOptional = notificationRepository.findById(notificationId);
        if(getBaseOptional.isEmpty()){
            return response.notFound(getBaseOptional);
        }
        return response.templateSukses(getBaseOptional);
    }

    public void sendNotification(Customer customer, String message) {
        Notification notification = new Notification();
        notification.setCustomer(customer);
        notification.setMessage(message);
        notification.setTimestamp(new Date());
        notificationRepository.save(notification);
    }
    @Scheduled(fixedRate = 60 * 1000) // setiap 1 menit
    public void sendPaymentReminder() {
        String paid = "false";
        Date dateNow = new Date();
        Calendar calNow = Calendar.getInstance();
        calNow.setTime(dateNow);
        // Ambil daftar booking yang belum dibayar setelah 2 jam
        List<Booking> unpaidBookings = bookingRepository.findUnpaidBookings(paid);
        for (Booking booking : unpaidBookings) {
            Date dateBooking = booking.getCreated_date();
            Calendar calBooking = Calendar.getInstance();
            calBooking.setTime(dateBooking);
            long hourDiff = (calNow.getTimeInMillis() - calBooking.getTimeInMillis()) / (60 * 60 * 1000);
            // Periksa apakah selisih waktu lebih dari 2 jam
            if (hourDiff >= 2) {
                // Kirim notifikasi ke pelanggan
                notificationService.sendNotification(booking.getCustomer(), "booking tiket anda kadaluarsa karena melebihi batas waktu pembayaran.");
                // Tandai bahwa notifikasi telah dikirim
                booking.setNotificationSent(true);
                bookingRepository.save(booking);
            }
        }
    }
}
