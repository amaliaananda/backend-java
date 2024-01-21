package com.backend.travelid.service;

import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    Map getByID(Long notificationId);
    List<Notification> getByCustomerId(Long customerId);
    List<Notification> getByCustomerEmail(String email);
    List<Notification> getAllNotifications();
    public void sendNotification(Customer customer, String message);
}
