package com.axcy.springcinema.repository;

import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.service.exception.EventNotFoundException;

import java.util.Collection;

public interface EventRepository {
    Event save(Event event) throws EventNotFoundException;
    void delete(long id);
    Event getByName(String name);

    Event getById(Long id) throws EventNotFoundException;

    Collection<Event> getAll();
}
