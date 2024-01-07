package com.backend.travelid.repository;

import com.backend.travelid.entity.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long>, JpaSpecificationExecutor<BookingDetail> {

    List<BookingDetail> findByCustomerName(String customerName);
}

