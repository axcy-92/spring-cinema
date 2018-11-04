package com.axcy.springcinema.entity;

public class Seat {
    private int number;
    private boolean vip;

    public Seat(int number, boolean vip) {
        this.number = number;
        this.vip = vip;
    }

    public Seat(int number) {
        this.number = number;
        this.vip = false;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "number=" + number +
                ", vip=" + vip +
                '}';
    }
}
