package com.axcy.springcinema.service.impl;

import com.axcy.springcinema.entity.Auditorium;
import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.repository.EventRepository;
import com.axcy.springcinema.service.EventService;
import com.axcy.springcinema.service.Rating;
import com.axcy.springcinema.service.exception.AuditoriumAlreadyAssignedException;
import com.axcy.springcinema.service.exception.EventNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    @Transactional("txManager")
    public Event create(Event event) throws EventNotFoundException {
        log.info("Event <" + event.getName() + "> created");
        return eventRepository.save(event);
    }

    @Override
    @Transactional("txManager")
    public Event create(String name, float price, Rating rating) throws EventNotFoundException {
        Event event = new Event(name, null, price, rating);
        log.info("Event <" + event.getName() + "> created");
        return eventRepository.save(event);
    }

    @Override
    @Transactional("txManager")
    public Event create(String name, LocalDateTime dateTime, float price, Rating rating) throws EventNotFoundException {
        Event event = new Event(name, dateTime, price, rating);
        log.info("Event <" + event.getName() + "> created");
        return eventRepository.save(event);
    }

    @Override
    @Transactional("txManager")
    public void remove(Event event) {
        if (event != null && event.getId()!=null) {
            eventRepository.delete(event.getId());
        }
    }

    @Override
    public Event getByName(String name) {
        return eventRepository.getByName(name);
    }

    @Override
    public Event getById(long id) throws EventNotFoundException {
        return eventRepository.getById(id);
    }

    @Override
    public Collection<Event> getAll() {
        return eventRepository.getAll();
    }

    @Override
    public Collection<Event> getForDateRange(LocalDateTime from, LocalDateTime to) {
        Collection<Event> events = eventRepository.getAll();
        return events.stream()
                .filter(e -> e.getDateTime().isAfter(from) && e.getDateTime().isBefore(to))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Event> getNextEvents(LocalDateTime to) {
        return getForDateRange(LocalDateTime.now(), to);
    }

    @Override
    @Transactional("txManager")
    public Event assignAuditorium(Event event, Auditorium auditorium, LocalDateTime dateTime) throws AuditoriumAlreadyAssignedException, EventNotFoundException {
        if (event != null && auditorium != null && dateTime != null) {
            long count = eventRepository.getAll().stream()
                    .filter(e -> e.getDateTime() != null && e.getDateTime().isEqual(dateTime))
                    .map(Event::getAuditorium)
                    .filter(Objects::nonNull)
                    .filter(a -> a.getId() == auditorium.getId())
                    .count();
            if (count != 0) {
                throw new AuditoriumAlreadyAssignedException(event, auditorium);
            }
            event.setAuditorium(auditorium);
            event.setDateTime(dateTime);
            eventRepository.save(event);

            log.info("Event <" + event.getName() + "> assigned to auditorium <" + auditorium.getName() + ">");
        }
        return event;
    }

}
