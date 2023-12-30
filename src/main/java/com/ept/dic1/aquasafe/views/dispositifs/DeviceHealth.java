package com.ept.dic1.aquasafe.views.dispositifs;

import com.ept.dic1.aquasafe.data.SampleDevice;

public class DeviceHealth {

    public enum Status {
        EXCELLENT, OK, FAILING;
    }

    private Status status;
    private String deviceTrackingNumber;

    public int getContaminantLevel() {
        return contaminantLevel;
    }

    private int contaminantLevel;
    private double batteryLevel;

    public DeviceHealth() {
        // Default constructor
    }

    public DeviceHealth(SampleDevice device) {
        this.deviceTrackingNumber = device.getTrackingNumber();
        if(device.getBatteryLevel() != null) {
            this.batteryLevel = device.getBatteryLevel();
        } else {
            this.batteryLevel = 0;
        }
        determineStatus();
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    private void determineStatus() {
        // Définir le statut en fonction du niveau de batterie
        if (batteryLevel >= 80) {
            status = Status.EXCELLENT;
        } else if (batteryLevel >= 50) {
            status = Status.OK;
        } else {
            status = Status.FAILING;
        }
    }
}
