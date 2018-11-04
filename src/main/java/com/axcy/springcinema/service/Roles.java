package com.axcy.springcinema.service;

public enum Roles {
    REGISTERED_USER("REGISTERED_USER"),
    BOOKING_MANAGER("BOOKING_MANAGER");

    private String desc;

    Roles(String desc) {
        this.desc = desc;
    }

    public String getDesc(){
        return desc;
    }
}
