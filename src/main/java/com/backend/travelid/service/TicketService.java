package com.backend.travelid.service;

import com.backend.travelid.entity.Ticket;

import java.util.List;
import java.util.Map;

public interface TicketService {
    List<Ticket> getAllTickets();
    List<Ticket> getTicketsByCustomerName(String customerName);
    public Map getByID(Long booking);
    Map saveTicket(Ticket ticket);
    Map updateTicket(Ticket ticket);
    Map deleteTicket(Ticket ticket);
}
