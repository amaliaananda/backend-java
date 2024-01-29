package com.backend.travelid.service.impl;

import com.backend.travelid.dto.*;
import com.backend.travelid.entity.*;
import com.backend.travelid.repository.CustomerRepository;
import com.backend.travelid.repository.BookingRepository;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.repository.BookingDetailRepository;
import com.backend.travelid.repository.SeatRepository;
import com.backend.travelid.service.SeatService;
import com.backend.travelid.service.BookingService;
import com.backend.travelid.service.NotificationService;
import com.backend.travelid.utils.Config;
import com.backend.travelid.utils.TemplateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private SeatService seatService;

    @Autowired
    public TemplateResponse response;

    @Autowired
    private NotificationService notificationService;

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> getByCustomerId(Long customerId) {
        try {
            log.info("get booking by user");
            if (customerId == null) {
                throw new RuntimeException(Config.ID_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(customerId);
            if (chekDataDBCustomer.isEmpty()) {
                throw new RuntimeException(Config.USER_NOT_FOUND);
            }
            List<Booking> getBaseOptional = bookingRepository.getByCustomer(chekDataDBCustomer);
            if(getBaseOptional.isEmpty()){
                throw new RuntimeException(Config.BOOKING_NOT_FOUND);
            }
            return getBaseOptional;
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
                return response.Error(Config.CUSTOMER_REQUIRED);
            }
            if(booking.getTotalPrice() == null){
                return response.Error(Config.TOTAL_PRICE_REQUIRED);
            }
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(booking.getCustomer().getId());
            if (chekDataDBCustomer.isEmpty()) {
                return response.Error(Config.CUSTOMER_NOT_FOUND);
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
            if (booking.getId() == null) return response.Error(Config.ID_REQUIRED);

            Optional<Booking> chekDataDBbooking = bookingRepository.findById(booking.getId());
            if (chekDataDBbooking.isEmpty()) return response.Error(Config.BOOKING_NOT_FOUND);

            if (booking.getCustomer()!= null) {
                Optional<Customer> chekDataDBCustomer = customerRepository.findById(booking.getCustomer().getId());
                if (chekDataDBCustomer.isEmpty()) return response.Error(Config.CUSTOMER_NOT_FOUND);
                chekDataDBbooking.get().setCustomer(booking.getCustomer());
            }
            if (booking.getTotalPrice()!= null) chekDataDBbooking.get().setTotalPrice(booking.getTotalPrice());
            if (booking.getPaid()!= null) chekDataDBbooking.get().setPaid(booking.getPaid());
            if (booking.getAddOnSelectingSeat()!= null) chekDataDBbooking.get().setAddOnSelectingSeat(booking.getAddOnSelectingSeat());
            if (booking.getAddOnLuggagePrice()!= null) chekDataDBbooking.get().setAddOnLuggagePrice(booking.getAddOnLuggagePrice());
            if (booking.getAddOnLuggage()!= null) chekDataDBbooking.get().setAddOnLuggage(booking.getAddOnLuggage());
            if (booking.getBankPembayaran()!= null) chekDataDBbooking.get().setBankPembayaran(booking.getBankPembayaran());
            if (booking.getNamaRekening()!= null) chekDataDBbooking.get().setNamaRekening(booking.getNamaRekening());
            if (booking.getMasaBerlaku()!= null) chekDataDBbooking.get().setMasaBerlaku(booking.getMasaBerlaku());
            if (booking.getCvvCvn()!= null) chekDataDBbooking.get().setCvvCvn(booking.getCvvCvn());
            if (booking.getNomorRekening()!= null) chekDataDBbooking.get().setNomorRekening(booking.getNomorRekening());
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
            if (booking.getId() == null) return response.Error(Config.ID_REQUIRED);

            Optional<Booking> chekDataDBbooking = bookingRepository.findById(booking.getId());
            if (chekDataDBbooking.isEmpty()) return response.Error(Config.BOOKING_NOT_FOUND);

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
    public Map saveRoundtripBookingWithDetails(BookingRoundtripRequestDTO bookingRoundtripRequestDTO) {
        try {
            log.info("save roundtrip booking with details");

            // Validasi input
            if (bookingRoundtripRequestDTO.getCustomer() == null)
                return response.Error(Config.CUSTOMER_REQUIRED);

            if (bookingRoundtripRequestDTO.getListOutboundBookingDetail() == null)
                return response.Error(Config.LIST_OUTBOUND_BOOKING_DETAIL_REQUIRED);

            if (bookingRoundtripRequestDTO.getListReturnBookingDetail() == null)
                return response.Error(Config.LIST_RETURN_BOOKING_DETAIL_REQUIRED);

            Optional<Customer> chekDataDBCustomer = customerRepository.findById(bookingRoundtripRequestDTO.getCustomer().getId());
            if (chekDataDBCustomer.isEmpty())
                return response.Error(Config.CUSTOMER_NOT_FOUND);

            Optional<Flight> chekDataDBOutboundFlight = flightRepository.findById(bookingRoundtripRequestDTO.getOutboundFlight().getId());
            if (chekDataDBOutboundFlight.isEmpty())
                return response.Error(Config.FLIGHT_NOT_FOUND);

            Optional<Flight> chekDataDBReturnFlight = flightRepository.findById(bookingRoundtripRequestDTO.getReturnFlight().getId());
            if (chekDataDBReturnFlight.isEmpty())
                return response.Error(Config.FLIGHT_NOT_FOUND);

            Flight outboundFlight = chekDataDBOutboundFlight.get();
            Flight returnFlight = chekDataDBReturnFlight.get();

            // cek tanggal pulang tak boleh lebih dulu dari tanggal berangkat
            if (outboundFlight.getFlightTime().after(returnFlight.getFlightTime()))
                return response.Error("Return Date must be at least after Outbound Date.");

            // Buat outbound booking
            Booking booking = new Booking();
            booking.setCustomer(chekDataDBCustomer.get());

            booking.setPaid("false");
            booking.setTotalPrice(0L);// Harga awal
            booking.setAddOnSelectingSeat(0L);

            // Proses tiap outbound booking detail
            for (BookingDetailDTO outboundBookingDetailDTO : bookingRoundtripRequestDTO.getListOutboundBookingDetail()) {
                BookingDetail bookingDetail = createBookingDetailRT(outboundFlight, outboundBookingDetailDTO);
                bookingDetail.setBooking(booking);
                booking.getBookingDetail().add(bookingDetail);
                // Tambahan biaya untuk pemilihan kursi
                if (outboundBookingDetailDTO.getSeatNumber() != null) {
                    Optional<Seat> chekDataDBSeat = seatRepository.getByFlightAndSeatBooked(outboundFlight, outboundBookingDetailDTO.getSeatNumber());
                    if (!chekDataDBSeat.isEmpty())
                        return response.Error(Config.SEAT_ALREADY_BOOKED);
                    // simpan data seat booked
                    seatService.saveSeat(outboundFlight, outboundBookingDetailDTO.getSeatNumber());
                    // Hitung total harga
                    booking.setTotalPrice(booking.getTotalPrice() + outboundBookingDetailDTO.getTotalSeatPrice());
                    booking.setAddOnSelectingSeat(booking.getAddOnSelectingSeat() +
                            (outboundBookingDetailDTO.getTotalSeatPrice() - outboundFlight.getPrice()));
                } else
                    // Hitung total harga
                    booking.setTotalPrice(booking.getTotalPrice() + calculateTotalPriceRT(outboundFlight, outboundBookingDetailDTO));
            }
            // Proses tiap return booking detail
            for (BookingDetailDTO returnBookingDetailDTO : bookingRoundtripRequestDTO.getListReturnBookingDetail()) {
                BookingDetail returnBookingDetail = createBookingDetailRT(returnFlight, returnBookingDetailDTO);
                returnBookingDetail.setBooking(booking);
                booking.getBookingDetail().add(returnBookingDetail);// Tambahan biaya untuk pemilihan kursi
                if (returnBookingDetailDTO.getSeatNumber() != null) {
                    Optional<Seat> chekDataDBSeat = seatRepository.getByFlightAndSeatBooked(returnFlight, returnBookingDetailDTO.getSeatNumber());
                    if (!chekDataDBSeat.isEmpty())
                        return response.Error(Config.SEAT_ALREADY_BOOKED);
                    // simpan data seat booked
                    seatService.saveSeat(returnFlight, returnBookingDetailDTO.getSeatNumber());
                    // Hitung total harga
                    booking.setTotalPrice(booking.getTotalPrice() + returnBookingDetailDTO.getTotalSeatPrice());
                    booking.setAddOnSelectingSeat(booking.getAddOnSelectingSeat() +
                            (returnBookingDetailDTO.getTotalSeatPrice() - returnFlight.getPrice()));
                } else
                    // Hitung total harga
                    booking.setTotalPrice(booking.getTotalPrice() + calculateTotalPriceRT(returnFlight, returnBookingDetailDTO));
            }
            // Simpan booking
            Booking savedBooking = bookingRepository.save(booking);

            // Kirim notifikasi booking berhasil
            notificationService.sendNotification(chekDataDBCustomer.get(), "Booking Roundtrip berhasil! Segera lakukan pembayaran sebelum 2 jam dari waktu booking!" );

            return response.templateSaveSukses(savedBooking);
        } catch (Exception e) {
            log.error("save roundtrip booking with details error: " + e.getMessage());
            throw new RuntimeException("save roundtrip booking with details =" + e.getMessage());
        }
    }
    @Override
    @Transactional
    public Map saveBookingWithDetails(BookingRequestDTO bookingRequestDTO) {
        try {
            log.info("save booking with details");
            // Validasi input
            if (bookingRequestDTO.getCustomer() == null)
                return response.Error(Config.CUSTOMER_REQUIRED);
            if (bookingRequestDTO.getListBookingDetail() == null)
                return response.Error(Config.LIST_BOOKING_DETAIL_REQUIRED);
            Optional<Customer> chekDataDBCustomer = customerRepository.findById(bookingRequestDTO.getCustomer().getId());
            if (chekDataDBCustomer.isEmpty())
                return response.Error(Config.CUSTOMER_NOT_FOUND);
            Optional<Flight> chekDataDBFlight = flightRepository.findById(bookingRequestDTO.getFlight().getId());
            if (chekDataDBFlight.isEmpty())
                return response.Error(Config.FLIGHT_NOT_FOUND);

            Flight flight = chekDataDBFlight.get();
            // Buat booking
            Booking booking = new Booking();
            booking.setCustomer(chekDataDBCustomer.get());
            booking.setAddOnLuggage(bookingRequestDTO.getAddOnLuggage());

            booking.setPaid("false");
            booking.setTotalPrice(0L);// Harga awal
            booking.setAddOnSelectingSeat(0L);

            // Proses tiap booking detail
            for (BookingDetailDTO bookingDetailDTO : bookingRequestDTO.getListBookingDetail()) {
                BookingDetail bookingDetail = createBookingDetail(bookingRequestDTO, bookingDetailDTO);
                bookingDetail.setBooking(booking);
                booking.getBookingDetail().add(bookingDetail);
                // Tambahan biaya untuk pemilihan kursi
                if (bookingDetailDTO.getSeatNumber() != null) {
                    Optional<Seat> chekDataDBSeat = seatRepository.getByFlightAndSeatBooked(flight, bookingDetailDTO.getSeatNumber());
                    if (!chekDataDBSeat.isEmpty())
                        return response.Error(Config.SEAT_ALREADY_BOOKED);
                    // simpan data seat booked
                    seatService.saveSeat(flight, bookingDetailDTO.getSeatNumber());
                    // Hitung total harga
                    booking.setTotalPrice(booking.getTotalPrice() + bookingDetailDTO.getTotalSeatPrice());
                    booking.setAddOnSelectingSeat(booking.getAddOnSelectingSeat() +
                            (bookingDetailDTO.getTotalSeatPrice() - flight.getPrice()));
                } else
                    // Hitung total harga
                    booking.setTotalPrice(booking.getTotalPrice() + calculateTotalPrice(bookingRequestDTO,bookingDetailDTO));
            }
            // Simpan booking
            Booking savedBooking = bookingRepository.save(booking);

            // Kirim notifikasi booking berhasil
            notificationService.sendNotification(chekDataDBCustomer.get(), "Booking berhasil! Segera lakukan pembayaran sebelum 2 jam dari waktu booking!" );

            return response.templateSaveSukses(savedBooking);
        } catch (Exception e) {
            log.error("save booking with details error: " + e.getMessage());
            throw new RuntimeException("save booking with details =" + e.getMessage());
        }
    }

    private BookingDetail createBookingDetail(BookingRequestDTO bookingRequestDTO, BookingDetailDTO bookingDetailDTO) {
        BookingDetail bookingDetail = new BookingDetail();
        bookingDetail.setFlight(flightRepository.findById(bookingRequestDTO.getFlight().getId())
                .orElseThrow(() -> new RuntimeException(Config.FLIGHT_NOT_FOUND)));

        bookingDetail.setCustomerName(bookingDetailDTO.getCustomerName());
        bookingDetail.setIdentityNumber(bookingDetailDTO.getIdentityNumber());

        if (bookingDetailDTO.getSeatNumber() != null)
            bookingDetail.setPrice(bookingDetailDTO.getTotalSeatPrice());
        else bookingDetail.setPrice(calculateTotalPrice(bookingRequestDTO,bookingDetailDTO));

        bookingDetail.setCategory(bookingDetailDTO.getCategory());

        if (bookingDetailDTO.getSeatNumber() != null)
            bookingDetail.setSeatNumber(bookingDetailDTO.getSeatNumber());

        // Set seat, jika kategori bukan "infant" maka menggunakan seat dari DTO,
        if ("business".equals(bookingDetail.getFlight().getPassengerClass()) && !"infant".equals(bookingDetailDTO.getCategory())){
            bookingDetail.setLuggage("30 kg");
        } else if ("economy".equals(bookingDetail.getFlight().getPassengerClass())&& !"infant".equals(bookingDetailDTO.getCategory())){
            bookingDetail.setLuggage("20 kg");
        } else if ("infant".equals(bookingDetailDTO.getCategory())){
            // jika "infant", maka menggunakan nilai default
            bookingDetail.setSeatNumber(null);
            bookingDetail.setLuggage("10 kg");
        } else throw new RuntimeException(Config.PASSENGER_CLASS_NOT_FOUND);
        return bookingDetail;
    }

    private Long calculateTotalPrice(BookingRequestDTO bookingRequestDTO, BookingDetailDTO bookingDetailDTO) {
        Long idFlight = bookingRequestDTO.getFlight().getId();
        Optional<Flight> chekDataDBFlight = flightRepository.findById(idFlight);
        if (chekDataDBFlight.isEmpty())
            throw new RuntimeException(Config.FLIGHT_NOT_FOUND);
        Flight flight = chekDataDBFlight.get();
        // Implementasi perhitungan harga sesuai kategori
        // Contoh: 10% dari harga untuk kategori infant
        if ("infant".equals(bookingDetailDTO.getCategory()))
            return (long) (0.1 * flight.getPrice());

        // Kategori lainnya
        return flight.getPrice();
    }
    private BookingDetail createBookingDetailRT(Flight flight, BookingDetailDTO bookingDetailDTO) {
        BookingDetail bookingDetail = new BookingDetail();
        bookingDetail.setFlight(flightRepository.findById(flight.getId())
                .orElseThrow(() -> new RuntimeException(Config.FLIGHT_NOT_FOUND)));

        bookingDetail.setCustomerName(bookingDetailDTO.getCustomerName());
        bookingDetail.setIdentityNumber(bookingDetailDTO.getIdentityNumber());

        if (bookingDetailDTO.getSeatNumber() != null)
            bookingDetail.setPrice(bookingDetailDTO.getTotalSeatPrice());
        else bookingDetail.setPrice(calculateTotalPriceRT(flight,bookingDetailDTO));

        bookingDetail.setCategory(bookingDetailDTO.getCategory());

        if (bookingDetailDTO.getSeatNumber() != null)
            bookingDetail.setSeatNumber(bookingDetailDTO.getSeatNumber());

        // Set seat, jika kategori bukan "infant" maka menggunakan seat dari DTO,
        if ("business".equals(bookingDetail.getFlight().getPassengerClass()) && !"infant".equals(bookingDetailDTO.getCategory())){
            bookingDetail.setLuggage("30 kg");
        } else if ("economy".equals(bookingDetail.getFlight().getPassengerClass())&& !"infant".equals(bookingDetailDTO.getCategory())){
            bookingDetail.setLuggage("20 kg");
        } else if ("infant".equals(bookingDetailDTO.getCategory())){
            // jika "infant", maka menggunakan nilai default
            bookingDetail.setSeatNumber(null);
            bookingDetail.setLuggage("10 kg");
        } else throw new RuntimeException(Config.PASSENGER_CLASS_NOT_FOUND);
        return bookingDetail;
    }

    private Long calculateTotalPriceRT(Flight flights, BookingDetailDTO bookingDetailDTO) {
        Long idFlight = flights.getId();
        Optional<Flight> chekDataDBFlight = flightRepository.findById(idFlight);
        if (chekDataDBFlight.isEmpty())
            throw new RuntimeException(Config.FLIGHT_NOT_FOUND);
        Flight flight = chekDataDBFlight.get();
        // Implementasi perhitungan harga sesuai kategori
        // Contoh: 10% dari harga untuk kategori infant
        if ("infant".equals(bookingDetailDTO.getCategory()))
            return (long) (0.1 * flight.getPrice());

        // Kategori lainnya
        return flight.getPrice();
    }
    public Map processPayment(PaymentRequestDTO paymentRequestDTO) {
        try {
            log.info("process Payment");
            // Validasi input
            if (paymentRequestDTO.getBooking() == null)
                return response.Error(Config.BOOKING_REQUIRED);
            Optional<Booking> chekDataDBBooking = bookingRepository.findById(paymentRequestDTO.getBooking().getId());
            if (chekDataDBBooking.isEmpty())
                return response.Error(Config.BOOKING_NOT_FOUND);
            if (chekDataDBBooking.get().isNotificationSent())
                return response.Error("this booking has expired, please rebook the flight");
            if (paymentRequestDTO.getBankPembayaran() == null)
                throw new RuntimeException("Bank pembayaran is required");
            if (paymentRequestDTO.getNomorRekening() == null)
                throw new RuntimeException("Nomor Rekening is required");
            if (paymentRequestDTO.getNamaRekening() == null)
                throw new RuntimeException("Nama Rekening is required");
            if (paymentRequestDTO.getMasaBerlaku() == null)
                throw new RuntimeException("Masa Berlaku is required");
            if (paymentRequestDTO.getCvvCvn() == null)
                throw new RuntimeException("Cvv Cvn is required");

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
    @Override
    public List<Booking> findByYearAndMonth(int year, int month) {
        LocalDateTime startDateTime = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endDateTime = startDateTime.plusMonths(1).minusSeconds(1);
        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return bookingRepository.findByCreated_dateBetween(startDate, endDate);
    }

    @Override
    public List<Booking> findByYear(int year) {
        LocalDateTime startDateTime = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endDateTime = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        Date startDate = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return bookingRepository.findByCreated_dateBetween(startDate, endDate);
    }
}
