package com.sample;


public class ParkingEvent {

    public static final int ENTER = 0;
    public static final int EXIT = 1;

    private String car;

    private String zone;

    private int type;

    public ParkingEvent() {

    }

    public ParkingEvent(String car, String zone, int type) {
        this.car = car;
        this.zone = zone;
        this.type = type;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ParkingEvent : car = " + car + ", zone = " + zone + ", type = " + type;
    }
}
