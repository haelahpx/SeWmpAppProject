package com.example.elaundryproject;

public class LaundryShop {
    public int id;
    public String address;
    public String name;
    public String phone;
    public double latitude;
    public double longitude;
    private double distance;


    public LaundryShop() {}

    public LaundryShop(int id, String address, String name, String phone, double latitude, double longitude) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter for address
    public String getAddress() {
        return address;
    }

    public int getId() {
        return id;
    }


    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for phone
    public String getPhone() {
        return phone;
    }

    // Getter for latitude
    public double getLatitude() {
        return latitude;
    }

    // Getter for longitude
    public double getLongitude() {
        return longitude;
    }

    // Getter for distance (optional)
    public double getDistance() {
        return distance;
    }

    // Setter for distance (optional)
    public void setDistance(double distance) {
        this.distance = distance;
    }

}
