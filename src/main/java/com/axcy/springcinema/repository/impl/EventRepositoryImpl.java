package com.axcy.springcinema.repository.impl;

import com.axcy.springcinema.entity.Event;
import com.axcy.springcinema.repository.AuditoriumRepository;
import com.axcy.springcinema.repository.EventRepository;
import com.axcy.springcinema.service.Rating;
import com.axcy.springcinema.service.exception.EventNotFoundException;
import com.axcy.springcinema.utils.Convert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Repository
public class EventRepositoryImpl implements EventRepository {

    private static final String UPDATE_BY_ID = "UPDATE event SET name=?, date=?, ticketPrice=?, rating=?, auditorium_id=? WHERE id=?";
    private static final String UPDATE_BY_NAME = "UPDATE event SET name=?, date=?, ticketPrice=?, rating=?, auditorium_id=? WHERE name=?";
    private static final String DELETE_BOOKED_TICKETS = "DELETE FROM tickets WHERE ticket_id IN (SELECT t.id FROM ticket t WHERE t.event_id=?)";
    private static final String DELETE_TICKETS_BY_EVENT_ID = "DELETE FROM ticket WHERE event_id=?";
    private static final String DELETE_EVENT = "DELETE FROM event WHERE id=?";
    private static final String SELECT_BY_ID = "SELECT * FROM event WHERE id=?";
    private static final String SELECT_BY_NAME = "SELECT * FROM event WHERE name=?";
    private static final String SELECT_ALL = "SELECT * FROM event";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AuditoriumRepository auditoriumRepository;

    @Override
    @Transactional("txManager")
    public Event save(Event event) throws EventNotFoundException {
        if (event != null) {
            int updatedRow = 0;
            if (event.getId() != null) {
                updatedRow = jdbcTemplate.update(UPDATE_BY_ID,
                        event.getName(),
                        Convert.toTimestamp(event.getDateTime()),
                        event.getTicketPrice(),
                        event.getRating().getValue(),
                        event.getAuditorium() != null ? event.getAuditorium().getId() : null,
                        event.getId());
                if (updatedRow != 0) {
                    event = getById(event.getId());
                }
            } else if (event.getName() != null) {
                updatedRow = jdbcTemplate.update(UPDATE_BY_NAME,
                        event.getName(),
                        Convert.toTimestamp(event.getDateTime()),
                        event.getTicketPrice(),
                        event.getRating().getValue(),
                        event.getAuditorium() != null ? event.getAuditorium().getId() : null,
                        event.getName());
                if (updatedRow != 0) {
                    event = getByName(event.getName());
                }
            }
            if (updatedRow == 0) {
                SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("event");
                insert.setGeneratedKeyName("id");
                Map<String, Object> args = new HashMap<>();
                args.put("name", event.getName());
                args.put("date", Convert.toTimestamp(event.getDateTime()));
                args.put("ticketPrice", event.getTicketPrice());
                args.put("rating", event.getRating().getValue());
                args.put("auditorium_id", event.getAuditorium() != null ? event.getAuditorium().getId() : null);
                event.setId(insert.executeAndReturnKey(args).longValue());
            }
        }
        return event;
    }

    @Override
    @Transactional("txManager")
    public void delete(long id) {
        jdbcTemplate.update(DELETE_BOOKED_TICKETS, id);
        jdbcTemplate.update(DELETE_TICKETS_BY_EVENT_ID, id);
        jdbcTemplate.update(DELETE_EVENT, id);
    }

    @Override
    public Event getByName(String name) {
        if (name != null) {
            try {
                return jdbcTemplate.queryForObject(SELECT_BY_NAME, eventMapper(), name);
            } catch (EmptyResultDataAccessException ignored) {
            }
        }
        return null;
    }

    @Override
    public Event getById(Long id) throws EventNotFoundException {
        if (id != null) {
            try {
                return jdbcTemplate.queryForObject(SELECT_BY_ID, eventMapper(), id);
            } catch (EmptyResultDataAccessException ignored) {
                throw new EventNotFoundException();
            }
        }
        return null;
    }

    @Override
    public Collection<Event> getAll() {
        return jdbcTemplate.query(SELECT_ALL, eventMapper());
    }

    private RowMapper<Event> eventMapper() {
        return (rs, rowNum) -> {
            Event event = new Event(
                    rs.getLong(1),
                    rs.getString(2),
                    ofNullable(rs.getTimestamp(3)).map(Timestamp::toLocalDateTime).orElse(null),
                    rs.getFloat(4),
                    Rating.getRating(rs.getInt(5))
            );
            event.setAuditorium(auditoriumRepository.getById(rs.getInt(6)));
            return event;
        };
    }
}
