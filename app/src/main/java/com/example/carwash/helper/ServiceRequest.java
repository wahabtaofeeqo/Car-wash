package com.example.carwash.helper;

public class ServiceRequest {
    private String address;
    private String carName;
    private int carThumbnail;
    private double latitude;
    private double longitude;
    private String date;
    private String email;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitufe) {
        this.latitude = latitufe;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCarThumbnail() {
        return carThumbnail;
    }

    public void setCarThumbnail(int carThumbnail) {
        this.carThumbnail = carThumbnail;
    }
}
