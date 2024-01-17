package com.backend.travelid.service.impl;

import com.backend.travelid.dto.BookingDetailDTO;
import com.backend.travelid.dto.BookingRequestDTO;
import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.BookingDetail;
import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.Flight;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.repository.BookingRepository;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.service.BookingService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    public TemplateResponse response;

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Map getByCustomerId(Long customerId) {
        try {
            log.info("get booking by user");
            if (customerId == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(customerId);
            if (chekDataDBCustomer.isEmpty()) {
                return response.Error(Config.USER_NOT_FOUND);
            }
            chekDataDBCustomer.get().setId(customerId);
            Optional<Booking> getBaseOptional = bookingRepository.getByCustomerId(customerId);
            if(getBaseOptional.isEmpty()){
                return response.notFound(getBaseOptional);
            }
            return response.templateSukses(getBaseOptional);
        }catch (Exception e){
            log.error("get booking by Customer error: "+e.getMessage());
            return response.Error("get booking by Customer ="+e.getMessage());
        }
    }

    @Override
    public Map getByID(Long booking) {
        Optional<Booking> getBaseOptional = bookingRepository.findById(booking);
        if(getBaseOptional.isEmpty()){
            return response.notFound(getBaseOptional);
        }
        return response.templateSukses(getBaseOptional);
    }

    @Override
    public Map saveBooking(Booking booking) {
        try {
            log.info("save booking");
            if(booking.getCustomer() == null){
                return response.Error(Config.CUSTOMER_REQUIRED);
            }
            if(booking.getTotalPrice() == null){
                return response.Error(Config.TOTAL_PRICE_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(booking.getCustomer().getId());
            if (chekDataDBCustomer.isEmpty()) {
                return response.Error(Config.CUSTOMER_NOT_FOUND);
            }
            booking.setPaid(false);
            return response.templateSaveSukses(bookingRepository.save(booking));
        }catch (Exception e){
            log.error("save booking error: "+e.getMessage());
            return response.Error("save booking ="+e.getMessage());
        }
    }

    @Override
    public Map updateBooking(Booking booking) {
        try {
            log.info("Update booking");
            if (booking.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<Booking> chekDataDBbooking = bookingRepository.findById(booking.getId());
            if (chekDataDBbooking.isEmpty()) {
                return response.Error(Config.BOOKING_NOT_FOUND);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(booking.getCustomer().getId());
            if (chekDataDBCustomer.isEmpty()) {
                return response.Error(Config.CUSTOMER_NOT_FOUND);
            }
            chekDataDBbooking.get().setCustomer(booking.getCustomer());
            chekDataDBbooking.get().setTotalPrice(booking.getTotalPrice());
            chekDataDBbooking.get().setPaid(booking.getPaid());
            chekDataDBbooking.get().setAddOnSelectingSeat(booking.getAddOnSelectingSeat());
            chekDataDBbooking.get().setAddOnLuggagePrice(booking.getAddOnLuggagePrice());
            chekDataDBbooking.get().setAddOnLuggage(booking.getAddOnLuggage());
            chekDataDBbooking.get().setBankPembayaran(booking.getBankPembayaran());
            chekDataDBbooking.get().setNamaRekening(booking.getNamaRekening());
            chekDataDBbooking.get().setMasaBerlaku(booking.getMasaBerlaku());
            chekDataDBbooking.get().setCvvCvn(booking.getCvvCvn());
            chekDataDBbooking.get().setNomorRekening(booking.getNomorRekening());
            chekDataDBbooking.get().setUpdated_date(new Date());

            return response.sukses(bookingRepository.save(chekDataDBbooking.get()));
        }catch (Exception e){
            log.error("Update booking error: "+e.getMessage());
            return response.Error("Update booking ="+e.getMessage());
        }
    }

    @Override
    public Map deleteBooking(Booking booking) {
        try {
            log.info("Delete booking");
            if (booking.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<Booking> chekDataDBbooking = bookingRepository.findById(booking.getId());
            if (chekDataDBbooking.isEmpty()) {
                return response.Error(Config.BOOKING_NOT_FOUND);
            }

            chekDataDBbooking.get().setDeleted_date(new Date());
            bookingRepository.save(chekDataDBbooking.get());
            return response.sukses(Config.SUCCESS);
        }catch (Exception e){
            log.error("Delete booking error: "+e.getMessage());
            return response.Error("Delete booking ="+e.getMessage());
        }
    }

    @Override
    @Transactional
    public Map saveBookingWithDetails(BookingRequestDTO bookingRequestDTO) {
        try {
            log.info("save booking with details");

            // Validasi input
            if (bookingRequestDTO.getCustomer() == null) {
                return response.Error(Config.CUSTOMER_REQUIRED);
            }
            if (bookingRequestDTO.getListBookingDetail() == null) {
                return response.Error(Config.LIST_BOOKING_DETAIL_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(bookingRequestDTO.getCustomer().getId());
            if (chekDataDBCustomer.isEmpty()) {
                return response.Error(Config.CUSTOMER_NOT_FOUND);
            }

            // Buat booking
            Booking booking = new Booking();
            booking.setCustomer(chekDataDBCustomer.get());
            booking.setAddOnLuggage(bookingRequestDTO.getAddOnLuggage());
            booking.setBankPembayaran(bookingRequestDTO.getBankPembayaran());
            booking.setNamaRekening(bookingRequestDTO.getNamaRekening());
            booking.setNomorRekening(bookingRequestDTO.getNomorRekening());
            booking.setMasaBerlaku(bookingRequestDTO.getMasaBerlaku());
            booking.setCvvCvn(bookingRequestDTO.getCvvCvn());

            booking.setPaid(false);
            booking.setTotalPrice(0L);// Harga awal
            booking.setAddOnSelectingSeat(0L);

            // Proses tiap booking detail
            for (BookingDetailDTO bookingDetailDTO : bookingRequestDTO.getListBookingDetail()) {
                Optional<Flight> chekDataDBFlight = flightRepository.findById(bookingDetailDTO.getFlight().getId());
                if (chekDataDBFlight.isEmpty()) {
                    throw new RuntimeException(Config.FLIGHT_NOT_FOUND);
                }
                Flight flight = chekDataDBFlight.get();

                BookingDetail bookingDetail = createBookingDetail(bookingDetailDTO);
                bookingDetail.setBooking(booking);
                booking.getBookingDetails().add(bookingDetail);

                // Tambahan biaya untuk pemilihan kursi
                if (bookingDetailDTO.getSeatNumber() != null) {
                    // Hitung total harga
                    booking.setTotalPrice(booking.getTotalPrice() + bookingDetailDTO.getTotalSeatPrice());
                    booking.setAddOnSelectingSeat(booking.getAddOnSelectingSeat() +
                            (bookingDetailDTO.getTotalSeatPrice() - flight.getPrice()));
                } else {
                    // Hitung total harga
                    booking.setTotalPrice(booking.getTotalPrice() + calculateTotalPrice(bookingDetailDTO));
                }

            }
            // Simpan booking
            Booking savedBooking = bookingRepository.save(booking);
            return response.templateSaveSukses(savedBooking);
        } catch (Exception e) {
            log.error("save booking with details error: " + e.getMessage());
            return response.Error("save booking with details =" + e.getMessage());
        }
    }

    private BookingDetail createBookingDetail(BookingDetailDTO bookingDetailDTO) {
        BookingDetail bookingDetail = new BookingDetail();
        bookingDetail.setFlight(flightRepository.findById(bookingDetailDTO.getFlight().getId())
                .orElseThrow(() -> new RuntimeException(Config.FLIGHT_NOT_FOUND)));

        bookingDetail.setCustomerName(bookingDetailDTO.getCustomerName());
        bookingDetail.setIdentityNumber(bookingDetailDTO.getIdentityNumber());

        if (bookingDetailDTO.getSeatNumber() != null)
            bookingDetail.setPrice(bookingDetailDTO.getTotalSeatPrice());
        else bookingDetail.setPrice(calculateTotalPrice(bookingDetailDTO));

        bookingDetail.setCategory(bookingDetailDTO.getCategory());

        if (bookingDetailDTO.getSeatNumber() != null)
            bookingDetail.setSeatNumber(bookingDetailDTO.getSeatNumber());
         else {
            // Jika seat tidak dipilih, atur seat sesuai ketersediaan dalam flight
            String availableSeat = findAvailableSeat();
            bookingDetail.setSeatNumber(availableSeat);
        }
        // Set seat, jika kategori bukan "infant" maka menggunakan seat dari DTO,
        if ("business".equals(bookingDetail.getFlight().getPassengerClass()) && !"infant".equals(bookingDetailDTO.getCategory())){
            bookingDetail.setLuggage("30 kg");
            bookingDetail.setSeatNumber(bookingDetailDTO.getSeatNumber());
        } else if ("economy".equals(bookingDetail.getFlight().getPassengerClass())&& !"infant".equals(bookingDetailDTO.getCategory())){
            bookingDetail.setLuggage("20 kg");
            bookingDetail.setSeatNumber(bookingDetailDTO.getSeatNumber());
        } else if ("infant".equals(bookingDetailDTO.getCategory())){
            // jika "infant", maka menggunakan nilai default
            bookingDetail.setSeatNumber(null);
            bookingDetail.setLuggage("10 kg");
        } else throw new RuntimeException(Config.PASSENGER_CLASS_NOT_FOUND);
        return bookingDetail;
    }

    private Long calculateTotalPrice(BookingDetailDTO bookingDetailDTO) {
        Long idFlight = bookingDetailDTO.getFlight().getId();
        Optional<Flight> chekDataDBFlight = flightRepository.findById(idFlight);
        if (chekDataDBFlight.isEmpty()) {
            throw new RuntimeException(Config.FLIGHT_NOT_FOUND);
        }
        Flight flight = chekDataDBFlight.get();
        // Implementasi perhitungan harga sesuai kategori
        // Contoh: 10% dari harga untuk kategori infant
        if ("infant".equals(bookingDetailDTO.getCategory())) {
            return (long) (0.1 * flight.getPrice());
        }
        // Kategori lainnya
        return flight.getPrice();
    }
    private String findAvailableSeat() {
        return "Choose When Checkin";
    }

}
