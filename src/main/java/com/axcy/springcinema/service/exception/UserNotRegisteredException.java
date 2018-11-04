package com.axcy.springcinema.service.exception;

public class UserNotRegisteredException extends Exception {
    public UserNotRegisteredException() {
        super("Sorry, user not found!");
    }
}
