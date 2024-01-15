package com.backend.travelid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private CustomerDTO customer;
    private String AddOnLuggage;
    private List<BookingDetailDTO> listBookingDetail;
}
