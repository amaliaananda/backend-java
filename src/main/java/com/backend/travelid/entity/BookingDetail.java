package com.backend.travelid.entity;

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
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "identity_number", nullable = false)
    private String identityNumber;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;

    @Column(name = "luggage")
    private String luggage;

}

