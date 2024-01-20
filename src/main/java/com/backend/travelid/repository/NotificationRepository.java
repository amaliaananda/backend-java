package com.backend.travelid.repository;


import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {
    @Query("SELECT n FROM Notification n JOIN n.customer c WHERE c.id = :customerId")
    public Optional<Notification> getByCustomerId(@Param("customerId") Long customerId);
}

