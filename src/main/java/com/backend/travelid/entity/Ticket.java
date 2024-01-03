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
@Table(name = "ticket")
@Where(clause = "deleted_date is null")
public class Ticket extends AbstractDate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "identity_number", nullable = false)
    private String identityNumber;

    @Column(name = "passenger_class")
    private String passengerClass;

    @Column(name = "seat_number")
    private String seatNumber;

    @Column(name = "luggage")
    private String luggage;

    @Column(name = "price")
    private Long price;

    @Column(name = "paid")
    private Boolean paid;

}

