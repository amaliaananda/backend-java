package com.backend.travelid.service.impl;

import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.BookingDetail;
import com.backend.travelid.entity.Flight;
import com.backend.travelid.repository.BookingDetailRepository;
import com.backend.travelid.repository.BookingRepository;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.service.BookingDetailService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
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
public class BookingDetailServiceImpl implements BookingDetailService {

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    public TemplateResponse response;

    @Override
    public List<BookingDetail> getAllBookingDetails() {
        return bookingDetailRepository.findAll();
    }

    @Override
    public Map getByCustomerName(String customerName) {
        try {
            log.info("getByCustomerName");
            return response.sukses(bookingDetailRepository.findByCustomerName(customerName));
        }catch (Exception e){
            log.error("getByCustomerName error: "+e.getMessage());
            throw new RuntimeException("getByCustomerName ="+e.getMessage());
        }
    }

    @Override
    public Map getByID(Long bookingDetail) {
        Optional<BookingDetail> getBaseOptional = bookingDetailRepository.findById(bookingDetail);
        if(getBaseOptional.isEmpty()){
            throw new NotFoundException("not found");
        }
        return response.templateSukses(getBaseOptional);
    }

    @Override
    public Map saveBookingDetail(BookingDetail bookingDetail) {
        try {
            log.info("save booking Detail");
            if(bookingDetail.getCustomerName() == null){
                throw new RuntimeException(Config.NAME_REQUIRED);
            }
            if(bookingDetail.getIdentityNumber() == null){
                throw new RuntimeException(Config.IDENTITY_NUMBER_REQUIRED);
            }
            if(bookingDetail.getBooking() == null){
                throw new RuntimeException(Config.BOOKING_REQUIRED);
            }
            if(bookingDetail.getPrice() == null){
                throw new RuntimeException(Config.PRICE_REQUIRED);
            }
            if(bookingDetail.getFlight() == null){
                throw new RuntimeException(Config.FLIGHT_REQUIRED);
            }
            Optional<Booking> chekDataDBBooking = bookingRepository.findById(bookingDetail.getBooking().getId());
            if (chekDataDBBooking.isEmpty()) {
                throw new NotFoundException(Config.BOOKING_NOT_FOUND);
            }
            Optional<Flight> chekDataDBFlight = flightRepository.findById(bookingDetail.getFlight().getId());
            if (chekDataDBFlight.isEmpty()) {
                throw new NotFoundException(Config.FLIGHT_NOT_FOUND);
            }
            return response.templateSaveSukses(bookingDetailRepository.save(bookingDetail));
        }catch (Exception e){
            log.error("save booking Detail error: "+e.getMessage());
            throw new RuntimeException("save booking Detail ="+e.getMessage());
        }
    }

    @Override
    public Map updateBookingDetail(BookingDetail bookingDetail) {
        try {
            log.info("Update booking Detail");
            if (bookingDetail.getId() == null) {
                throw new RuntimeException(Config.ID_REQUIRED);
            }
            Optional<BookingDetail> chekDataDBbookingDetail = bookingDetailRepository.findById(bookingDetail.getId());
            if (chekDataDBbookingDetail.isEmpty()) {
                throw new NotFoundException(Config.BOOKING_DETAIL_NOT_FOUND);
            }
            Optional<Flight> chekDataDBFlight = flightRepository.findById(bookingDetail.getFlight().getId());
            if (chekDataDBFlight.isEmpty()) {
                throw new NotFoundException(Config.FLIGHT_NOT_FOUND);
            }
            Optional<Booking> chekDataDBBooking = bookingRepository.findById(bookingDetail.getBooking().getId());
            if (chekDataDBBooking.isEmpty()) {
                throw new NotFoundException(Config.BOOKING_NOT_FOUND);
            }
            chekDataDBbookingDetail.get().setFlight(bookingDetail.getFlight());
            chekDataDBbookingDetail.get().setCustomerName(bookingDetail.getCustomerName());
            chekDataDBbookingDetail.get().setIdentityNumber(bookingDetail.getIdentityNumber());
            chekDataDBbookingDetail.get().setSeatNumber(bookingDetail.getSeatNumber());
            chekDataDBbookingDetail.get().setPrice(bookingDetail.getPrice());
            chekDataDBbookingDetail.get().setLuggage(bookingDetail.getLuggage());
            chekDataDBbookingDetail.get().setUpdated_date(new Date());

            return response.sukses(bookingDetailRepository.save(chekDataDBbookingDetail.get()));
        }catch (Exception e){
            log.error("Update booking Detail error: "+e.getMessage());
            throw new RuntimeException("Update booking Detail ="+e.getMessage());
        }
    }

    @Override
    public Map deleteBookingDetail(BookingDetail bookingDetail) {
        try {
            log.info("Delete booking Detail");
            if (bookingDetail.getId() == null) {
                throw new RuntimeException(Config.ID_REQUIRED);
            }
            Optional<BookingDetail> chekDataDBbookingDetail = bookingDetailRepository.findById(bookingDetail.getId());
            if (chekDataDBbookingDetail.isEmpty()) {
                throw new NotFoundException(Config.BOOKING_DETAIL_NOT_FOUND);
            }

            chekDataDBbookingDetail.get().setDeleted_date(new Date());
            bookingDetailRepository.save(chekDataDBbookingDetail.get());
            return response.sukses(Config.SUCCESS);
        }catch (Exception e){
            log.error("Delete booking Detail error: "+e.getMessage());
            throw new RuntimeException("Delete booking Detail ="+e.getMessage());
        }
    }
}
