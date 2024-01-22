package com.backend.travelid.service.impl;

import com.backend.travelid.entity.Airlines;
import com.backend.travelid.entity.Flight;
import com.backend.travelid.repository.AirlinesRepository;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import com.backend.travelid.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class FlightServiceImpl implements FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirlinesRepository airlinesRepository;

    @Autowired
    private TemplateResponse response;

    @Override
    public List<Flight> getFlightsByAirline(String airline) {
        return flightRepository.getByAirline(airline);
    }

    @Override
    public List<Flight> getFlightsByPassengerClass(String passengerClass) {
        return flightRepository.findByPassengerClass(passengerClass);
    }

    @Override
    public Map getByID(Long flight) {
        Optional<Flight> getBaseOptional = flightRepository.findById(flight);
        if(getBaseOptional.isEmpty()){
            throw new NotFoundException(Config.FLIGHT_NOT_FOUND);
        }
        return response.templateSukses(getBaseOptional);
    }

    @Override
    public Map saveFlight(Flight flight) {
        try {
            log.info("save flight");
            if(flight.getPassengerClass() == null){
                return response.Error(Config.PASSENGER_CLASS_REQUIRED);
            }
            if(flight.getPrice() == null){
                return response.Error(Config.PRICE_REQUIRED);
            }
            if(flight.getAirlines() == null){
                return response.Error(Config.AIRLINE_REQUIRED);
            }
            if(flight.getOriginAirport() == null){
                return response.Error(Config.ORIGIN_AIRPORT_REQUIRED);
            }
            if(flight.getDestinationAirport() == null){
                return response.Error(Config.DESTINATION_AIRPORT_REQUIRED);
            }
            if(flight.getFlightNumber() == null){
                return response.Error(Config.FLIGHT_NUMBER_REQUIRED);
            }
            if(flight.getOriginCity() == null){
                return response.Error(Config.ORIGIN_CITY_REQUIRED);
            }
            if(flight.getDestinationCity() == null){
                return response.Error(Config.DESTINATION_CITY_REQUIRED);
            }
            if(flight.getFlightTime() == null){
                return response.Error(Config.FLIGHT_TIME_REQUIRED);
            }
            if(flight.getArrivedTime() == null){
                return response.Error(Config.ARRIVED_TIME_REQUIRED);
            }
            if(flight.getDuration() == null){
                return response.Error(Config.DURATION_REQUIRED);
            }
            if(flight.getTransit() == null){
                return response.Error(Config.TRANSIT_REQUIRED);
            }
            if(flight.getFreeMeal() == null){
                return response.Error(Config.FREEMEAL_REQUIRED);
            }
            Optional<Airlines> chekDataDBAirline = airlinesRepository.findById(flight.getAirlines().getId());
            if (chekDataDBAirline.isEmpty()) {
                return response.Error(Config.AIRLINE_NOT_FOUND);
            }
            if ("economy".equals(flight.getPassengerClass())) flight.setLuggage("20 kg");
            else if ("business".equals(flight.getPassengerClass())) flight.setLuggage("30 kg");
            else throw new RuntimeException(Config.PASSWORD_NOT_VALID);

            return response.templateSaveSukses(flightRepository.save(flight));
        }catch (Exception e){
            log.error("save flight error: "+e.getMessage());
            throw new RuntimeException("save flight ="+e.getMessage());
        }
    }

    @Override
    public Map updateFlight(Flight flight) {
        try {
            log.info("Update flight");
            if (flight.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<Flight> chekDataDBFlight = flightRepository.findById(flight.getId());
            if (chekDataDBFlight.isEmpty()) {
                return response.Error(Config.FLIGHT_NOT_FOUND);
            }
            Optional<Airlines> chekDataDBAirline = airlinesRepository.findByAirline(flight.getAirlines().getAirline());
            if (chekDataDBAirline.isEmpty()) {
                return response.Error(Config.AIRLINE_NOT_FOUND);
            }
            chekDataDBFlight.get().setAirlines(flight.getAirlines());
            chekDataDBFlight.get().setPassengerClass(flight.getPassengerClass());
            chekDataDBFlight.get().setPrice(flight.getPrice());
            chekDataDBFlight.get().setOriginAirport(flight.getOriginAirport());
            chekDataDBFlight.get().setDestinationAirport(flight.getDestinationAirport());
            chekDataDBFlight.get().setFlightNumber(flight.getFlightNumber());
            chekDataDBFlight.get().setOriginCity(flight.getOriginCity());
            chekDataDBFlight.get().setDestinationCity(flight.getDestinationCity());
            chekDataDBFlight.get().setGate(flight.getGate());
            chekDataDBFlight.get().setFlightTime(flight.getFlightTime());
            chekDataDBFlight.get().setArrivedTime(flight.getArrivedTime());
            chekDataDBFlight.get().setDuration(flight.getDuration());
            chekDataDBFlight.get().setTransit(flight.getTransit());
            chekDataDBFlight.get().setLuggage(flight.getLuggage());
            chekDataDBFlight.get().setFreeMeal(flight.getFreeMeal());
            chekDataDBFlight.get().setUpdated_date(new Date());

            return response.sukses(flightRepository.save(chekDataDBFlight.get()));
        }catch (Exception e){
            log.error("Update flight error: "+e.getMessage());
            throw new RuntimeException("Update flight ="+e.getMessage());
        }
    }

    @Override
    public Map deleteFlight(Flight flight) {
        try {
            log.info("Delete flight");
            if (flight.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<Flight> chekDataDBFlight = flightRepository.findById(flight.getId());
            if (chekDataDBFlight.isEmpty()) {
                return response.Error(Config.FLIGHT_NOT_FOUND);
            }

            chekDataDBFlight.get().setDeleted_date(new Date());
            flightRepository.save(chekDataDBFlight.get());
            return response.sukses(Config.SUCCESS);
        }catch (Exception e){
            log.error("Delete Flight error: "+e.getMessage());
            throw new RuntimeException("Delete Flight ="+e.getMessage());
        }
    }
}
