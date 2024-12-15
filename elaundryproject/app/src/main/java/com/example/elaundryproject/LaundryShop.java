package com.example.elaundryproject;

public class LaundryShop {
    public String name;
    public String address;
    public String phone;
    private double distance;
    public double latitude;
    public double longitude;

    public LaundryShop() {}

    public LaundryShop(String name, String address, String phone, double distance, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.distance = distance;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
