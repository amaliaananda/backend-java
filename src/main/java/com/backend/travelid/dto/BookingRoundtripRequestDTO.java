package com.backend.travelid.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRoundtripRequestDTO {
    private CustomerDTO customer;
    private FlightDTO outboundFlight;
    private FlightDTO returnFlight;
    private String AddOnLuggage;
    private String bankPembayaran;
    private String namaRekening;
    private String nomorRekening;
    private String masaBerlaku;
    private String cvvCvn;
    private List<BookingDetailDTO> listOutboundBookingDetail;
    private List<BookingDetailDTO> listReturnBookingDetail;
}
