package com.backend.travelid.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "booking_detail")
@Where(clause = "deleted_date is null")
public class BookingDetail extends AbstractDate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

//    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "identity_number", nullable = false)
    private String identityNumber;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "luggage")
    private String luggage;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "category", nullable = false)
    private String category;

}

