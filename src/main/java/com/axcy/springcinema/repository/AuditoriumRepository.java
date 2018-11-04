package com.axcy.springcinema.repository;

import com.axcy.springcinema.entity.Auditorium;
import com.axcy.springcinema.entity.Seat;

import java.util.Collection;
import java.util.List;

public interface AuditoriumRepository {
    Auditorium getById(int id);
    List<Auditorium> getAuditoriums();
    long getSeatsNumber(int auditoriumId);
    Collection<Seat> getSeats(int auditoriumId);
    Seat getSeatByAuditoriumIdAndNumber(int auditoriumId, int number);
}
