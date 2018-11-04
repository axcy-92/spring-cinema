package com.axcy.springcinema.repository;

import com.axcy.springcinema.entity.Ticket;
import com.axcy.springcinema.entity.user.User;

import java.util.Collection;

public interface TicketRepository {
    Ticket save(Ticket ticket);
    Collection<Ticket> getAll();
    Collection<Ticket> getByEventName(String eventName);
    Collection<Ticket> getBookedTickets();
    Collection<Ticket> getBookedTicketsByUserId(long userId);
    void saveBookedTicket(User user, Ticket ticket);
    void deleteBookedTicketByUserId(long userId);
}
