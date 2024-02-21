package org.lab1java.sunsetsunriseapi.model;

import java.time.LocalDate;

public class SunInfoRequest {
    private double latitude;
    private double longitude;
    private LocalDate date;

    public SunInfoRequest(double latitude, double longitude, LocalDate date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public SunInfoRequest() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
