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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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
            throw new InternalError("not found");
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
            throw new RuntimeException("save booking Detail ="+e.getMessage());
        }
    }

    @Override
    public Map updateBookingDetail(BookingDetail bookingDetail) {
        try {
            log.info("Update booking Detail");
            if (bookingDetail.getId() == null)
                return response.Error(Config.ID_REQUIRED);

            Optional<BookingDetail> chekDataDBbookingDetail = bookingDetailRepository.findById(bookingDetail.getId());
            if (chekDataDBbookingDetail.isEmpty())
                return response.Error(Config.BOOKING_DETAIL_NOT_FOUND);

            if (bookingDetail.getFlight() != null) {
                Optional<Flight> chekDataDBFlight = flightRepository.findById(bookingDetail.getFlight().getId());
                if (chekDataDBFlight.isEmpty())
                    return response.Error(Config.FLIGHT_NOT_FOUND);
                chekDataDBbookingDetail.get().setFlight(bookingDetail.getFlight());
            }
            if (bookingDetail.getFlight() != null) {
                Optional<Booking> chekDataDBBooking = bookingRepository.findById(bookingDetail.getBooking().getId());
                if (chekDataDBBooking.isEmpty())
                    return response.Error(Config.BOOKING_NOT_FOUND);
                chekDataDBbookingDetail.get().setBooking(bookingDetail.getBooking());
            }
            if (bookingDetail.getCustomerName() != null)chekDataDBbookingDetail.get().setCustomerName(bookingDetail.getCustomerName());
            if (bookingDetail.getIdentityNumber() != null)chekDataDBbookingDetail.get().setIdentityNumber(bookingDetail.getIdentityNumber());
            if (bookingDetail.getSeatNumber() != null)chekDataDBbookingDetail.get().setSeatNumber(bookingDetail.getSeatNumber());
            if (bookingDetail.getPrice() != null)chekDataDBbookingDetail.get().setPrice(bookingDetail.getPrice());
            if (bookingDetail.getLuggage() != null)chekDataDBbookingDetail.get().setLuggage(bookingDetail.getLuggage());
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
            if (bookingDetail.getId() == null)
                throw new InternalError(Config.ID_REQUIRED);

            Optional<BookingDetail> chekDataDBbookingDetail = bookingDetailRepository.findById(bookingDetail.getId());
            if (chekDataDBbookingDetail.isEmpty())
                throw new InternalError(Config.BOOKING_DETAIL_NOT_FOUND);

            chekDataDBbookingDetail.get().setDeleted_date(new Date());
            bookingDetailRepository.save(chekDataDBbookingDetail.get());
            return response.sukses(Config.SUCCESS);
        }catch (Exception e){
            log.error("Delete booking Detail error: "+e.getMessage());
            throw new RuntimeException("Delete booking Detail ="+e.getMessage());
        }
    }
    @Override
    public List<BookingDetail> findByYearAndMonth(int year, int month) {
        LocalDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDateTime = startDateTime.plusMonths(1).minusSeconds(1);
        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return bookingDetailRepository.findByCreatedDateBetweenAndPaidTrue(startDate, endDate);
    }
    @Override
    public List<BookingDetail> findByYear(int year) {
        LocalDateTime startDateTime = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return bookingDetailRepository.findByCreatedDateBetweenAndPaidTrue(startDate, endDate);
    }
}
