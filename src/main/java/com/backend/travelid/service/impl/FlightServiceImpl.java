package com.backend.travelid.service.impl;

import com.backend.travelid.entity.Flight;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import com.backend.travelid.service.FlightService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private TemplateResponse response;

    @Override
    public List<Flight> getFlightsByAirline(String airline) {
        return flightRepository.findByAirline(airline);
    }

    @Override
    public Map getByID(Long flight) {
        Optional<Flight> getBaseOptional = flightRepository.findById(flight);
        if(getBaseOptional.isEmpty()){
            return response.notFound(getBaseOptional);
        }
        return response.templateSukses(getBaseOptional);
    }

    @Override
    public Map saveFlight(Flight flight) {
        try {
            log.info("save flight");
            if(flight.getAirline() == null){
                return response.Error(Config.AIRLINE_REQUIRED);
            }
            if(flight.getOriginAirport() == null){
                return response.Error(Config.AIRPORT_REQUIRED);
            }
            if(flight.getDestinationAirport() == null){
                return response.Error(Config.AIRPORT_REQUIRED);
            }
            return response.sukses(flightRepository.save(flight));
        }catch (Exception e){
            log.error("save flight error: "+e.getMessage());
            return response.Error("save flight ="+e.getMessage());
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
            chekDataDBFlight.get().setOriginAirport(flight.getOriginAirport());
            chekDataDBFlight.get().setDestinationAirport(flight.getDestinationAirport());
            chekDataDBFlight.get().setAirline(flight.getAirline());
            chekDataDBFlight.get().setFlightNumber(flight.getFlightNumber());
            chekDataDBFlight.get().setOriginCity(flight.getOriginCity());
            chekDataDBFlight.get().setDestinationCity(flight.getDestinationCity());
            chekDataDBFlight.get().setGate(flight.getGate());
            chekDataDBFlight.get().setFlightTime(flight.getFlightTime());
            chekDataDBFlight.get().setArrivedTime(flight.getArrivedTime());
            chekDataDBFlight.get().setDuration(flight.getDuration());
            chekDataDBFlight.get().setLuggage(flight.getLuggage());
            chekDataDBFlight.get().setFreeMeal(flight.getFreeMeal());
            chekDataDBFlight.get().setUpdated_date(new Date());

            return response.sukses(flightRepository.save(chekDataDBFlight.get()));
        }catch (Exception e){
            log.error("Update flight error: "+e.getMessage());
            return response.Error("Update flight ="+e.getMessage());
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
            return response.Error("Delete Flight ="+e.getMessage());
        }
    }
}
