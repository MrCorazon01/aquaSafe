package com.ept.dic1.aquasafe.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


import java.time.LocalDate;

@Entity
public class SampleDevice {
    @Id
    private String trackingNumber;
    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double conductivity;
    private Double turbidity;
    private Double pH;
    private String region;
    private LocalDate installationDate;
    private Double batteryLevel;

    public Double getBatteryLevel() {
        return batteryLevel;
    }



    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getConductivity() {
        return conductivity;
    }

    public void setConductivity(Double conductivity) {
        this.conductivity = conductivity;
    }

    public Double getTurbidity() {
        return turbidity;
    }

    public void setTurbidity(Double turbidity) {
        this.turbidity = turbidity;
    }

    public Double getpH() {
        return pH;
    }

    public void setpH(Double pH) {
        this.pH = pH;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDate getInstallationDate() {
        return installationDate;
    }

    public void setInstallationDate(LocalDate installationDate) {
        this.installationDate = installationDate;
    }
}
