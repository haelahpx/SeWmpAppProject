package com.example.elaundryproject;

public class LaundryShop {
    public String address;
    public String name;
    public String phone;
    public double latitude;
    public double longitude;
    private double distance;

    public LaundryShop() {}

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }
}
