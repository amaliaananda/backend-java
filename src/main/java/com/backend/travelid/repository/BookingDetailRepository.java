package com.backend.travelid.repository;

import com.backend.travelid.entity.BookingDetail;
import com.backend.travelid.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long>, JpaSpecificationExecutor<BookingDetail> {

    List<BookingDetail> findByCustomerName(String customerName);
    Optional<BookingDetail> findBySeatNumberAndFlight(String seatNumber, Flight flight);
}

