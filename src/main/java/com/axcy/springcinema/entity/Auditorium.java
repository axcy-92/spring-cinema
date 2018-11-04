package com.axcy.springcinema.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.rangeClosed;

public class Auditorium {
    private int id;
    private String name;
    private int numberOfSeats;
    private Collection<Seat> seats;

    public Auditorium(int id) {
        this.id = id;
    }

    public Auditorium(int id, String name, int numberOfSeats, String vipSeats) {
        this.seats = new ArrayList<>();
        this.id = id;
        this.name = name;
        this.numberOfSeats = numberOfSeats;

        Collection<Integer> vipList = Stream.of(vipSeats.split(",")).map(Integer::valueOf).collect(toList());
        rangeClosed(1, numberOfSeats).forEach(n -> seats.add(new Seat(n, vipList.contains(n))));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public Collection<Seat> getSeats() {
        return seats;
    }

    public void setSeats(Collection<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Auditorium{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", numberOfSeats=" + numberOfSeats +
                ", seats=" + seats +
                '}';
    }
}
