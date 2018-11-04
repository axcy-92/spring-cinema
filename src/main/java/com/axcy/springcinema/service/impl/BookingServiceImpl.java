package com.axcy.springcinema.service.impl;

import com.axcy.springcinema.entity.Auditorium;
import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.entity.Seat;
import com.axcy.springcinema.entity.Ticket;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.entity.user.UserAccount;
import com.axcy.springcinema.repository.AuditoriumRepository;
import com.axcy.springcinema.repository.TicketRepository;
import com.axcy.springcinema.service.*;
import com.axcy.springcinema.service.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private static final int VIP_PRICE_COEF = 2;
    private static final float HIGH_EVENT_PRICE_COEF = 1.2f;
    @Autowired
    private UserService userService;
    @Autowired
    private UserAccountService userAccountService;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private DiscountService discountService;
    @Autowired
    private AuditoriumRepository auditoriumRepository;

    @Override
    public float getTicketPrice(Event event, LocalDateTime dateTime, Collection<Integer> seatNumbers, User user) throws
            UserNotRegisteredException, EventNotAssignedException {
        float price = 0;
        Optional.ofNullable(user)
                .flatMap(u -> Optional.ofNullable(userService.getUserByName(u.getName())))
                .orElseThrow(UserNotRegisteredException::new);

        Optional.ofNullable(event)
                .flatMap(e -> Optional.ofNullable(e.getAuditorium()))
                .orElseThrow(EventNotAssignedException::new);

        Auditorium auditorium = event.getAuditorium();
        Collection<Seat> auditoriumSeats = auditorium.getSeats();
        for (int number : seatNumbers) {
            Seat seat = auditoriumRepository.getSeatByAuditoriumIdAndNumber(auditorium.getId(), number);
            if (auditoriumSeats.contains(seat)) {
                price = (seat.isVip() ? event.getTicketPrice() * VIP_PRICE_COEF : event.getTicketPrice());
            }
            price = price * (event.getRating() == Rating.HIGH ? HIGH_EVENT_PRICE_COEF : 1);
            float discount = discountService.getDiscount(user, event, dateTime);
            price = price - (100 * discount);
        }

        return price;
    }

    @Override
    @Transactional("txManager")
    public void bookTicket(User user, Ticket ticket) throws UserNotRegisteredException, TicketAlreadyBookedException,
            TicketWithoutEventException, LackOfFundsException {
        //if users is null or not registered then throw exception
        Optional.ofNullable(user)
                .flatMap(u -> Optional.ofNullable(userService.getUserByName(u.getName())))
                .orElseThrow(UserNotRegisteredException::new);

        //if ticket is null or without event then throw exception
        Optional.ofNullable(ticket)
                .flatMap(t -> Optional.ofNullable(t.getEvent()))
                .orElseThrow(TicketWithoutEventException::new);

        //false if ticket already booked for specified event and seat
        boolean notBooked = ticketRepository.getBookedTickets().stream()
                .noneMatch(t -> t.getEvent().getName().equals(ticket.getEvent().getName()) &&
                                t.getSeat().getNumber() == ticket.getSeat().getNumber()
                );
        if (notBooked) {
            //withdrawal ticket price from user account
            UserAccount userAccount = userAccountService.getAccountByUser(user);
            userAccountService.doPayment(userAccount, ticket.getPrice());
            ticketRepository.saveBookedTicket(user, ticket);
            log.info(String.format("User <%s> booked ticket with seat number %d for event <%s>",
                    user.getName(),
                    ticket.getSeat().getNumber(),
                    ticket.getEvent().getName()));
        } else {
            throw new TicketAlreadyBookedException(ticket.getSeat().getNumber());
        }
    }

    @Override
    public Collection<Ticket> getTicketsForEvent(Event event, LocalDateTime dateTime) {
        return ticketRepository.getByEventName(event.getName()).stream()
                .filter(t -> t.getEvent().getDateTime().isEqual(dateTime))
                .collect(Collectors.toList());
    }
}
