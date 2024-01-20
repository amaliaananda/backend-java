package com.backend.travelid.service.impl;

import com.backend.travelid.dto.BookingDetailDTO;
import com.backend.travelid.dto.BookingRequestDTO;
import com.backend.travelid.dto.PaymentRequestDTO;
import com.backend.travelid.entity.Booking;
import com.backend.travelid.entity.BookingDetail;
import com.backend.travelid.entity.Customer;
import com.backend.travelid.entity.Flight;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.repository.BookingRepository;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.service.BookingService;
import com.backend.travelid.service.NotificationService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Autowired
    private NotificationService notificationService;

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Map getByCustomerId(Long customerId) {
        try {
            log.info("get booking by user");
            if (customerId == null) {
                throw new RuntimeException(Config.ID_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(customerId);
            if (chekDataDBCustomer.isEmpty()) {
                throw new RuntimeException(Config.USER_NOT_FOUND);
            }
            chekDataDBCustomer.get().setId(customerId);
            Optional<Booking> getBaseOptional = bookingRepository.getByCustomerId(customerId);
            if(getBaseOptional.isEmpty()){
                return response.notFound(getBaseOptional);
            }
            return response.templateSukses(getBaseOptional);
        }catch (Exception e){
            log.error("get booking by Customer error: "+e.getMessage());
            throw new RuntimeException("get booking by Customer ="+e.getMessage());
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
                throw new RuntimeException(Config.CUSTOMER_REQUIRED);
            }
            if(booking.getTotalPrice() == null){
                throw new RuntimeException(Config.TOTAL_PRICE_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(booking.getCustomer().getId());
            if (chekDataDBCustomer.isEmpty()) {
                throw new RuntimeException(Config.CUSTOMER_NOT_FOUND);
            }
            booking.setPaid("false");
            // Kirim notifikasi booking berhasil
            notificationService.sendNotification(chekDataDBCustomer.get(), "Booking berhasil! Silakan segera lakukan pembayaran sebelum "+ LocalDateTime.now().plusHours(2) );
            return response.templateSaveSukses(bookingRepository.save(booking));
        }catch (Exception e){
            log.error("save booking error: "+e.getMessage());
            throw new RuntimeException("save booking ="+e.getMessage());
        }
    }

    @Override
    public Map updateBooking(Booking booking) {
        try {
            log.info("Update booking");
            if (booking.getId() == null) {
                throw new RuntimeException(Config.ID_REQUIRED);
            }
            Optional<Booking> chekDataDBbooking = bookingRepository.findById(booking.getId());
            if (chekDataDBbooking.isEmpty()) {
                throw new RuntimeException(Config.BOOKING_NOT_FOUND);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(booking.getCustomer().getId());
            if (chekDataDBCustomer.isEmpty()) {
                throw new RuntimeException(Config.CUSTOMER_NOT_FOUND);
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
            throw new RuntimeException("Update booking ="+e.getMessage());
        }
    }

    @Override
    public Map deleteBooking(Booking booking) {
        try {
            log.info("Delete booking");
            if (booking.getId() == null) {
                throw new RuntimeException(Config.ID_REQUIRED);
            }
            Optional<Booking> chekDataDBbooking = bookingRepository.findById(booking.getId());
            if (chekDataDBbooking.isEmpty()) {
                throw new RuntimeException(Config.BOOKING_NOT_FOUND);
            }

            chekDataDBbooking.get().setDeleted_date(new Date());
            bookingRepository.save(chekDataDBbooking.get());
            return response.sukses(Config.SUCCESS);
        }catch (Exception e){
            log.error("Delete booking error: "+e.getMessage());
            throw new RuntimeException("Delete booking ="+e.getMessage());
        }
    }

    @Override
    @Transactional
    public Map saveBookingWithDetails(BookingRequestDTO bookingRequestDTO) {
        try {
            log.info("save booking with details");

            // Validasi input
            if (bookingRequestDTO.getCustomer() == null) {
                throw new RuntimeException(Config.CUSTOMER_REQUIRED);
            }
            if (bookingRequestDTO.getListBookingDetail() == null) {
                throw new RuntimeException(Config.LIST_BOOKING_DETAIL_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(bookingRequestDTO.getCustomer().getId());
            if (chekDataDBCustomer.isEmpty()) {
                throw new RuntimeException(Config.CUSTOMER_NOT_FOUND);
            }

            // Buat booking
            Booking booking = new Booking();
            booking.setCustomer(chekDataDBCustomer.get());
            booking.setAddOnLuggage(bookingRequestDTO.getAddOnLuggage());

            booking.setPaid("false");
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
                booking.getBookingDetail().add(bookingDetail);

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

            // Kirim notifikasi booking berhasil
            notificationService.sendNotification(chekDataDBCustomer.get(), "Booking berhasil! Silakan segera lakukan pembayaran sebelum "+ LocalDateTime.now().plusHours(2) );

            return response.templateSaveSukses(savedBooking);
        } catch (Exception e) {
            log.error("save booking with details error: " + e.getMessage());
            throw new RuntimeException("save booking with details =" + e.getMessage());
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


    public Map processPayment(PaymentRequestDTO paymentRequestDTO) {
        try {
            log.info("process Payment");
            // Validasi input
            if (paymentRequestDTO.getBooking() == null) {
                throw new RuntimeException(Config.BOOKING_REQUIRED);
            }
            Optional<Booking> chekDataDBBooking = bookingRepository.findById(paymentRequestDTO.getBooking().getId());
            if (chekDataDBBooking.isEmpty()) {
                throw new RuntimeException(Config.BOOKING_NOT_FOUND);
            }
            if (paymentRequestDTO.getBankPembayaran() == null) {
                throw new RuntimeException("Bank pembayaran is required");
            }
            if (paymentRequestDTO.getNomorRekening() == null) {
                throw new RuntimeException("Nomor Rekening is required");
            }
            if (paymentRequestDTO.getNamaRekening() == null) {
                throw new RuntimeException("Nama Rekening is required");
            }
            if (paymentRequestDTO.getMasaBerlaku() == null) {
                throw new RuntimeException("Masa Berlaku is required");
            }
            if (paymentRequestDTO.getCvvCvn() == null) {
                throw new RuntimeException("Cvv Cvn is required");
            }
            // Update status pembayaran
            chekDataDBBooking.get().setPaid("true");
            chekDataDBBooking.get().setBankPembayaran(paymentRequestDTO.getBankPembayaran());
            chekDataDBBooking.get().setNamaRekening(paymentRequestDTO.getNamaRekening());
            chekDataDBBooking.get().setMasaBerlaku(paymentRequestDTO.getMasaBerlaku());
            chekDataDBBooking.get().setCvvCvn(paymentRequestDTO.getCvvCvn());
            chekDataDBBooking.get().setNomorRekening(paymentRequestDTO.getNomorRekening());
            chekDataDBBooking.get().setUpdated_date(new Date());

            // Simpan perubahan
            Booking savedBooking = bookingRepository.save(chekDataDBBooking.get());

            // Kirim notifikasi pembayaran berhasil
            notificationService.sendNotification(chekDataDBBooking.get().getCustomer(), "Pembayaran berhasil! Tiket Anda sudah dikonfirmasi.");

            return response.templateSaveSukses(savedBooking);
        } catch (Exception e) {
            log.error("process Payment error: " + e.getMessage());
            throw new RuntimeException("process Payment =" + e.getMessage());
        }
    }

}
