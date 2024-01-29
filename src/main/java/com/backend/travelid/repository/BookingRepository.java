package com.backend.travelid.repository;

import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

    List <Booking> getByCustomer(Optional<Customer> customer);

    @Query("SELECT b FROM Booking b WHERE b.created_date BETWEEN :startDate AND :endDate")
    List<Booking> findByCreatedDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT b FROM Booking b WHERE b.paid = :paid AND b.notificationSent = false")
    List<Booking> findUnpaidBookings(@Param("paid") String paid);

    @Query("SELECT MONTH(b.created_date) AS month, SUM(b.totalPrice) AS income " +
            "FROM Booking b " +
            "WHERE YEAR(b.created_date) = :year " +
            "GROUP BY MONTH(b.created_date)")
    List<Object[]> findIncomeByMonthAndYear(@Param("year") int year);

}

