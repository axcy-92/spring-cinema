package com.axcy.springcinema.service.exception;

public class EventNotFoundException extends Exception{
    public EventNotFoundException() {
        super("Sorry, event not found!");
    }
}
