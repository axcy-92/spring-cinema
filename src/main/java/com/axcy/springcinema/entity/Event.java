package com.axcy.springcinema.entity;

import com.axcy.springcinema.service.Rating;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class Event {
    private Long id;
    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateTime;

    private float ticketPrice;
    private Rating rating;
    private Auditorium auditorium;

    public Event() {}

    public Event(long id, String name, LocalDateTime dateTime, float ticketPrice, Rating rating) {
        this.id = id;
        this.name = name;
        this.dateTime = dateTime;
        this.ticketPrice = ticketPrice;
        this.rating = rating;
    }

    public Event(String name, LocalDateTime dateTime, float ticketPrice, Rating rating) {
        this.name = name;
        this.dateTime = dateTime;
        this.ticketPrice = ticketPrice;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public float getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(float ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public Auditorium getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(Auditorium auditorium) {
        this.auditorium = auditorium;
    }

    @Override
    public String toString() {
        return "Event : { id:" + getId() + ";"
                + "name:" + getName() + ";"
                + "dateTime:" + (getDateTime() != null ? getDateTime().toString() : "") + ";"
                + "ticketPrice:" + getTicketPrice() + ";"
                + "rating:" + (getRating() != null ? getRating().toString() : "") + ";"
                + "auditorium:" + (getAuditorium() != null ? getAuditorium().toString() : "") + " }";
    }
}
