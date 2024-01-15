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
            return response.Error("getByCustomerName ="+e.getMessage());
        }
    }

    @Override
    public Map getByID(Long bookingDetail) {
        Optional<BookingDetail> getBaseOptional = bookingDetailRepository.findById(bookingDetail);
        if(getBaseOptional.isEmpty()){
            return response.notFound(getBaseOptional);
        }
        return response.templateSukses(getBaseOptional);
    }

    @Override
    public Map saveBookingDetail(BookingDetail bookingDetail) {
        try {
            log.info("save booking Detail");
            if(bookingDetail.getCustomerName() == null){
                return response.Error(Config.NAME_REQUIRED);
            }
            if(bookingDetail.getIdentityNumber() == null){
                return response.Error(Config.IDENTITY_NUMBER_REQUIRED);
            }
            if(bookingDetail.getBooking() == null){
                return response.Error(Config.BOOKING_REQUIRED);
            }
            if(bookingDetail.getPrice() == null){
                return response.Error(Config.PRICE_REQUIRED);
            }
            if(bookingDetail.getFlight() == null){
                return response.Error(Config.FLIGHT_REQUIRED);
            }
            Optional<Booking> chekDataDBBooking = bookingRepository.findById(bookingDetail.getBooking().getId());
            if (chekDataDBBooking.isEmpty()) {
                return response.Error(Config.BOOKING_NOT_FOUND);
            }
            Optional<Flight> chekDataDBFlight = flightRepository.findById(bookingDetail.getFlight().getId());
            if (chekDataDBFlight.isEmpty()) {
                return response.Error(Config.FLIGHT_NOT_FOUND);
            }
            return response.templateSaveSukses(bookingDetailRepository.save(bookingDetail));
        }catch (Exception e){
            log.error("save booking Detail error: "+e.getMessage());
            return response.Error("save booking Detail ="+e.getMessage());
        }
    }

    @Override
    public Map updateBookingDetail(BookingDetail bookingDetail) {
        try {
            log.info("Update booking Detail");
            if (bookingDetail.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<BookingDetail> chekDataDBbookingDetail = bookingDetailRepository.findById(bookingDetail.getId());
            if (chekDataDBbookingDetail.isEmpty()) {
                return response.Error(Config.BOOKING_DETAIL_NOT_FOUND);
            }
            Optional<Flight> chekDataDBFlight = flightRepository.findById(bookingDetail.getFlight().getId());
            if (chekDataDBFlight.isEmpty()) {
                return response.Error(Config.FLIGHT_NOT_FOUND);
            }
            Optional<Booking> chekDataDBBooking = bookingRepository.findById(bookingDetail.getBooking().getId());
            if (chekDataDBBooking.isEmpty()) {
                return response.Error(Config.BOOKING_NOT_FOUND);
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
            return response.Error("Update booking Detail ="+e.getMessage());
        }
    }

    @Override
    public Map deleteBookingDetail(BookingDetail bookingDetail) {
        try {
            log.info("Delete booking Detail");
            if (bookingDetail.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<BookingDetail> chekDataDBbookingDetail = bookingDetailRepository.findById(bookingDetail.getId());
            if (chekDataDBbookingDetail.isEmpty()) {
                return response.Error(Config.BOOKING_DETAIL_NOT_FOUND);
            }

            chekDataDBbookingDetail.get().setDeleted_date(new Date());
            bookingDetailRepository.save(chekDataDBbookingDetail.get());
            return response.sukses(Config.SUCCESS);
        }catch (Exception e){
            log.error("Delete booking Detail error: "+e.getMessage());
            return response.Error("Delete booking Detail ="+e.getMessage());
        }
    }
}
