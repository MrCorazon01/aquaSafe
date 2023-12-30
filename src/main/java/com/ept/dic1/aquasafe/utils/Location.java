package com.ept.dic1.aquasafe.utils;

import java.time.LocalDate;

public class Location {

    private String trackingNumber;
    private String region;
    private double latitude;
    private double longitude;

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getRegion() {
        return region;
    }

    public Location(String trackingNumber, String region, double latitude, double longitude) {
        this.trackingNumber = trackingNumber;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}