package com.backend.travelid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailDTO {
    private String customerName;
    private String identityNumber;
    private String seatNumber;
    private Long totalSeatPrice;
    private String category; // adult, child, infant
}

