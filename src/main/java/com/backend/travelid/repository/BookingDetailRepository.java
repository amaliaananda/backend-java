package com.backend.travelid.repository;

import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.BookingDetail;
import com.backend.travelid.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long>, JpaSpecificationExecutor<BookingDetail> {

    List<BookingDetail> findByCustomerName(String customerName);
    @Query("SELECT bd FROM BookingDetail bd WHERE bd.created_date BETWEEN :startDate AND :endDate")
    List<BookingDetail> findByCreatedDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}

