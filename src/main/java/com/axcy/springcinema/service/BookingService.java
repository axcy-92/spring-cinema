package com.axcy.springcinema.service;

import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.entity.Ticket;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.service.exception.*;

import java.time.LocalDateTime;
import java.util.Collection;

public interface BookingService {
    float getTicketPrice(Event event, LocalDateTime dateTime, Collection<Integer> seatNumbers, User user) throws UserNotRegisteredException, EventNotAssignedException;

    void bookTicket(User user, Ticket ticket) throws UserNotRegisteredException, TicketAlreadyBookedException, TicketWithoutEventException, LackOfFundsException;

    Collection<Ticket> getTicketsForEvent(Event event, LocalDateTime dateTime);
}
