package com.backend.travelid.service.impl;

import com.backend.travelid.entity.Ticket;
import com.backend.travelid.repository.FlightRepository;
import com.backend.travelid.repository.TicketRepository;
import com.backend.travelid.service.TicketService;
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
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    public TemplateResponse response;

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getTicketsByCustomerName(String customerName) {
        return ticketRepository.findByCustomerName(customerName);
    }

    @Override
    public Map getByID(Long booking) {
        Optional<Ticket> getBaseOptional = ticketRepository.findById(booking);
        if(getBaseOptional.isEmpty()){
            return response.notFound(getBaseOptional);
        }
        return response.templateSukses(getBaseOptional);
    }

    @Override
    public Map saveTicket(Ticket ticket) {
        try {
            log.info("save Ticket");
            if(ticket.getCustomerName() == null){
                return response.Error(Config.NAME_REQUIRED);
            }
            if(ticket.getIdentityNumber() == null){
                return response.Error(Config.IDENTITY_NUMBER_REQUIRED);
            }
            return response.sukses(ticketRepository.save(ticket));
        }catch (Exception e){
            log.error("save Ticket error: "+e.getMessage());
            return response.Error("save Ticket ="+e.getMessage());
        }
    }

    @Override
    public Map updateTicket(Ticket ticket) {
        try {
            log.info("Update Ticket");
            if (ticket.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<Ticket> chekDataDBTicket = ticketRepository.findById(ticket.getId());
            if (chekDataDBTicket.isEmpty()) {
                return response.Error(Config.TICKET_NOT_FOUND);
            }

            chekDataDBTicket.get().setCustomerName(ticket.getCustomerName());
            chekDataDBTicket.get().setIdentityNumber(ticket.getIdentityNumber());
            chekDataDBTicket.get().setPassengerClass(ticket.getPassengerClass());
            chekDataDBTicket.get().setSeatNumber(ticket.getSeatNumber());
            chekDataDBTicket.get().setLuggage(ticket.getLuggage());
            chekDataDBTicket.get().setPrice(ticket.getPrice());
            chekDataDBTicket.get().setPaid(ticket.getPaid());
            chekDataDBTicket.get().setUpdated_date(new Date());

            return response.sukses(ticketRepository.save(chekDataDBTicket.get()));
        }catch (Exception e){
            log.error("Update Ticket error: "+e.getMessage());
            return response.Error("Update Ticket ="+e.getMessage());
        }
    }

    @Override
    public Map deleteTicket(Ticket ticket) {
        try {
            log.info("Delete Ticket");
            if (ticket.getId() == null) {
                return response.Error(Config.ID_REQUIRED);
            }
            Optional<Ticket> chekDataDBTicket = ticketRepository.findById(ticket.getId());
            if (chekDataDBTicket.isEmpty()) {
                return response.Error(Config.TICKET_NOT_FOUND);
            }

            chekDataDBTicket.get().setDeleted_date(new Date());
            ticketRepository.save(chekDataDBTicket.get());
            return response.sukses(Config.SUCCESS);
        }catch (Exception e){
            log.error("Delete Ticket error: "+e.getMessage());
            return response.Error("Delete Ticket ="+e.getMessage());
        }
    }
}
