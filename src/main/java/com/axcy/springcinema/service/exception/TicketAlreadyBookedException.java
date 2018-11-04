package com.axcy.springcinema.service.exception;

public class TicketAlreadyBookedException extends Exception {
    private String message;

    public TicketAlreadyBookedException(int seatNumber) {
        message = String.format("Ticket for seat number %d  already booked!", seatNumber);
    }

    @Override
    public String getMessage() {
        return message;
    }
}
