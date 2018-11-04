package com.axcy.springcinema.web.controller;

import com.axcy.springcinema.entity.Auditorium;
import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.repository.AuditoriumRepository;
import com.axcy.springcinema.service.EventService;
import com.axcy.springcinema.service.Rating;
import com.axcy.springcinema.service.exception.AuditoriumAlreadyAssignedException;
import com.axcy.springcinema.service.exception.EventNotFoundException;
import com.axcy.springcinema.web.view.EventPdfBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author Aleksei_Cherniavskii
 */

@Controller
@RequestMapping({"/event"})
public class EventController {

    @Autowired
    EventService eventService;
    @Autowired
    AuditoriumRepository auditoriumRepository;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String newEventForm(ModelMap model) throws Exception {
        model.addAttribute("eventForm", new Event());
        model.addAttribute("ratingList", Arrays.asList(Rating.LOW, Rating.MID, Rating.HIGH));
        model.addAttribute("auditoriumList", auditoriumRepository.getAuditoriums());
        model.addAttribute("action", "create");
        return "event/event-form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String newEventSave(@ModelAttribute("eventForm") Event event,
                               @RequestParam("auditoriumId") int auditoriumId) throws Exception {
        Auditorium auditorium = auditoriumRepository.getById(auditoriumId);
        if (auditorium != null) {
            eventService.assignAuditorium(eventService.create(event), auditorium, LocalDateTime.now());
        }
        return "redirect:/event/all";
    }

    @PostAuthorize("hasRole('BOOKING_MANAGER')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String deleteEvent(@PathVariable("id") int id) throws Exception {
        eventService.remove(eventService.getById(id));
        return "redirect:/event/all";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String editEventForm(@PathVariable("id") int id, ModelMap model) throws Exception {
        Event event = eventService.getById(id);
        model.addAttribute("eventForm", event);
        model.addAttribute("ratingList", Arrays.asList(Rating.LOW, Rating.MID, Rating.HIGH));
        model.addAttribute("auditoriumList", auditoriumRepository.getAuditoriums());
        model.addAttribute("eventAuditoriumId", event.getAuditorium() != null ? event.getAuditorium().getId() : null);
        model.addAttribute("action", "edit");
        return "event/event-form";
    }

    //TODO: fix datetime
    @RequestMapping(value = "/assign", method = RequestMethod.POST)
    public String assignAuditorium(@RequestParam("auditoriumId") int auditoriumId,
                                   //@DateTimeFormat(pattern = "dd/MM/yyyy hh:mm:ss")
                                   //@RequestParam("datetime")LocalDateTime dateTime,
                                   @RequestParam("eventId")int eventId) throws AuditoriumAlreadyAssignedException, EventNotFoundException {
        eventService.assignAuditorium(eventService.getById(eventId),
                                      auditoriumRepository.getById(auditoriumId),
                                      LocalDateTime.now()/*TODO*/);
        return "redirect:/event/view/" + eventId;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public String showAllEvents(ModelMap model) throws Exception {
        model.addAttribute("eventList", eventService.getAll());
        return "event/events";
    }

    @RequestMapping(value = "/downloadPDF", method = RequestMethod.GET)
    public ModelAndView downloadPDF() {
        Collection<Event> eventList = eventService.getAll();
        return new ModelAndView(new EventPdfBuilder(), "eventList", eventList);
    }
}
