package com.backend.travelid.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "flight")
@Where(clause = "deleted_date is null")
public class Flight extends AbstractDate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "origin_airport", nullable = false)
    private String originAirport;

    @Column(name = "destination_airport", nullable = false)
    private String destinationAirport;

    @Column(name = "airline", nullable = false)
    private String airline;

    @Column(name = "flight_number")
    private String flightNumber;

    @Column(name = "origin_city")
    private String originCity;

    @Column(name = "destination_city")
    private String destinationCity;

    @Column(name = "gate")
    private String gate;

    @Column(name = "flight_time")
    private String flightTime;

    @Column(name = "arrived_time")
    private String arrivedTime;

    @Column(name = "duration")
    private String duration;

    @Column(name = "luggage")
    private String luggage;

    @Column(name = "free_meal")
    private Boolean freeMeal;

}

