package com.axcy.springcinema.web.controller;

import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.entity.Seat;
import com.axcy.springcinema.entity.Ticket;
import com.axcy.springcinema.entity.user.User;
import com.axcy.springcinema.repository.AuditoriumRepository;
import com.axcy.springcinema.service.BookingService;
import com.axcy.springcinema.service.EventService;
import com.axcy.springcinema.service.Roles;
import com.axcy.springcinema.service.UserService;
import com.axcy.springcinema.web.view.TicketPdfBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Aleksei_Cherniavskii
 */

@Controller
@RequestMapping({"/booking"})
public class BookingController {

    private static final Logger LOG = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    EventService eventService;
    @Autowired
    UserService userService;
    @Autowired
    BookingService bookingService;
    @Autowired
    AuditoriumRepository auditoriumRepository;

    @RequestMapping(method = RequestMethod.GET)
    public String bookingForm(ModelMap model, HttpServletRequest request) throws Exception {
        if (request.isUserInRole(Roles.BOOKING_MANAGER.getDesc())) {
            Collection<User> userList = userService.getAll();
            model.addAttribute("userList", userList);
        } else {
            User currentUser = userService.getUserByName(request.getUserPrincipal().getName());
            model.addAttribute("userId", currentUser.getId());
        }
        Collection<Event> eventList = eventService.getAll();
        model.addAttribute("eventList", eventList);
        return "ticket/booking";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String bookingProcess(@RequestParam("eventId") int eventId,
                                 @RequestParam("userId") int userId,
                                 @RequestParam("seat-number") int seatNumber,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes) throws Exception {
        User user = userService.getById(userId);
        Event event = eventService.getById(eventId);
        Seat seat = auditoriumRepository.getSeatByAuditoriumIdAndNumber(event.getAuditorium().getId(), seatNumber);
        Ticket ticket = new Ticket();
        if (user != null && seat != null) {
            float price = bookingService.getTicketPrice(event,
                                                        event.getDateTime(),
                                                        Collections.singletonList(seat.getNumber()),
                                                        user);
            ticket.setEvent(event);
            ticket.setPrice(price);
            ticket.setSeat(seat);
            bookingService.bookTicket(user, ticket);

            redirectAttributes.addFlashAttribute("message", ticket.getPrice() + "$ has been debited from your account");
            return "redirect:/booking/result/event/" + event.getId();
        }
        //TODO: show tickets for user
        return "redirect:/";
    }

    @RequestMapping(value = "/result/event/{id}", method = RequestMethod.GET)
    public String bookingResult(@PathVariable("id") int eventId,
                                ModelMap model) throws Exception {
        Event event = eventService.getById(eventId);
        Collection<Ticket> ticketList = bookingService.getTicketsForEvent(event, event.getDateTime());
        model.addAttribute("ticketList", ticketList);
        model.addAttribute("event", event);
        return "ticket/book-tickets";
    }

    @RequestMapping(value = "/result/event/{id}/downloadPDF", method = RequestMethod.GET)
    public ModelAndView downloadPDF(@PathVariable("id") int eventId) throws Exception {
        Event event = eventService.getById(eventId);
        Collection<Ticket> ticketList = bookingService.getTicketsForEvent(event, event.getDateTime());
        return new ModelAndView(new TicketPdfBuilder(), "ticketList", ticketList);
    }
}
