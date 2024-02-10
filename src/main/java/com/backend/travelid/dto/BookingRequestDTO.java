package com.backend.travelid.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequestDTO {
    private CustomerDTO customer;
    private FlightDTO flight;
    private String AddOnLuggage;
    private String bankPembayaran;
    private String namaRekening;
    private String nomorRekening;
    private String masaBerlaku;
    private String cvvCvn;
    private String email;
    private String phoneNumber;
    private List<BookingDetailDTO> listBookingDetail;
}
