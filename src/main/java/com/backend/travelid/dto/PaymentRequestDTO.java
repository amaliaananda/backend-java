package com.backend.travelid.dto;

import com.backend.travelid.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Booking booking;
    private String bankPembayaran;
    private String namaRekening;
    private String nomorRekening;
    private String masaBerlaku;
    private String cvvCvn;
}
