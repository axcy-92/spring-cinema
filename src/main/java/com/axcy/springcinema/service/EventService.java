package com.axcy.springcinema.service;

import com.axcy.springcinema.entity.Auditorium;
import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.service.exception.AuditoriumAlreadyAssignedException;
import com.axcy.springcinema.service.exception.EventNotFoundException;

import java.time.LocalDateTime;
import java.util.Collection;

public interface EventService {

    Event create(Event event) throws EventNotFoundException;
    Event create(String name, float price, Rating rating) throws EventNotFoundException;

    Event create(String name, LocalDateTime dateTime, float price, Rating rating) throws EventNotFoundException;

    void remove(Event event);
    Event getByName(String name);

    Event getById(long id) throws EventNotFoundException;

    Collection<Event> getAll();
    Collection<Event> getForDateRange(LocalDateTime from, LocalDateTime to);
    Collection<Event> getNextEvents(LocalDateTime to);
    Event assignAuditorium(Event event, Auditorium auditorium, LocalDateTime dateTime) throws
            AuditoriumAlreadyAssignedException, EventNotFoundException;
}
