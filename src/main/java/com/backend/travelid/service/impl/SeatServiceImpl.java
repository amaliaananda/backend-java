package com.backend.travelid.service.impl;

import com.backend.travelid.controller.fileupload.FileStorageService;
import com.backend.travelid.entity.*;
import com.backend.travelid.entity.oauth.User;
import com.backend.travelid.repository.SeatRepository;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.service.SeatService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TemplateResponse response;

    @Override
    public void saveSeat(Flight flight, String seatBooked) {
        Seat seat = new Seat();
        seat.setFlight(flight);
        seat.setSeatBooked(seatBooked);
        seatRepository.save(seat);
    }
    @Override
    public Map getByID(Long idSeat) {
        Optional<Seat> getBaseOptional = seatRepository.findById(idSeat);
        if(getBaseOptional.isEmpty()){
            throw new InternalError(Config.SEAT_NOT_FOUND);
        }
        return response.templateSukses(getBaseOptional);
    }
    @Override
    public List<Seat> getByFlight(Long flightId) {
        try {
            if (flightId == null) {
                throw new InternalError(Config.ID_REQUIRED);
            }
            Optional<Flight> chekDataDBFlight = flightRepository.findById(flightId);
            if(chekDataDBFlight.isEmpty()){
                throw new InternalError(Config.FLIGHT_NOT_FOUND);
            }
            List<Seat> getBaseOptional = seatRepository.getByFlight(chekDataDBFlight);
            if(getBaseOptional.isEmpty()){
                throw new InternalError(Config.BOOKING_NOT_FOUND);
            }
            return getBaseOptional;
        }catch (Exception e){
            log.error("get booking by Customer error: "+e.getMessage());
            throw new RuntimeException("get booking by Customer ="+e.getMessage());
        }
    }
}
