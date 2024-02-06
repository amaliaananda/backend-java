package com.backend.travelid.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flight")
@Where(clause = "deleted_date is null AND flight_time > current_timestamp")
public class Flight extends AbstractDate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "airlines_id")
    private Airlines airlines;

    @Column(name = "passenger_class", nullable = false)
    private String passengerClass;

    @Column(name = "origin_airport", nullable = false)
    private String originAirport;

    @Column(name = "destination_airport", nullable = false)
    private String destinationAirport;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @Column(name = "origin_city", nullable = false)
    private String originCity;

    @Column(name = "destination_city", nullable = false)
    private String destinationCity;

    @Column(name = "gate", nullable = false)
    private String gate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Asia/Jakarta")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "flight_time", nullable = false)
    private Date flightTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "Asia/Jakarta")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "arrived_time", nullable = false)
    private Date arrivedTime;

    @Column(name = "duration", nullable = false)
    private String duration;

    @Column(name = "transit", nullable = false)
    private String transit;

    @Column(name = "luggage", nullable = false)
    private String luggage;

    @Column(name = "free_meal", nullable = false)
    private String freeMeal;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "discount_price")
    private Long discountPrice;

    @Column(name = "is_discount")
    private String isDiscount;

}

