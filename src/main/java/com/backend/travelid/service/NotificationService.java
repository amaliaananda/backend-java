package com.backend.travelid.service;

import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.Notification;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    Map getByID(Long notificationId);
    Map getByCustomerId(Long customerId);
    List<Notification> getAllNotifications();
    public void sendNotification(Customer customer, String message);
}
