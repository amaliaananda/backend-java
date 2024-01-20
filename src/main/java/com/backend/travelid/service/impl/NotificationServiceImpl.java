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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        notification.setTimestamp(LocalDateTime.now());
        notificationRepository.save(notification);
    }
    @Scheduled(fixedRate = 60 * 1000) // setiap 1 menit
    public void sendPaymentReminder() {
        LocalDateTime twoHoursAgo = LocalDateTime.now().minusHours(2);

        // Ambil daftar booking yang belum dibayar setelah 2 jam
        List<Booking> unpaidBookings = bookingRepository.findByPaidIsNullAndCreatedDateBeforeAndNotificationSentFalse(twoHoursAgo);

        for (Booking booking : unpaidBookings) {
            // Kirim notifikasi ke pelanggan
            notificationService.sendNotification(booking.getCustomer(), "booking tiket anda kadaluarsa karena melebihi batas waktu pembayaran.");

            // Tandai bahwa notifikasi telah dikirim
            booking.setNotificationSent(true);
            bookingRepository.save(booking);
        }
    }
}
