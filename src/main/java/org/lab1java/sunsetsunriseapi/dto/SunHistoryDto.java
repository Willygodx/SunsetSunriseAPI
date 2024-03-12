package org.lab1java.sunsetsunriseapi.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class SunHistoryDto {
    private double latitude;
    private double longitude;
    private LocalDate date;
    private LocalTime sunrise;
    private LocalTime sunset;
    private String timeZone;
    private String country;
    private String city;

    public SunHistoryDto(double latitude, double longitude, LocalDate date, LocalTime sunrise, LocalTime sunset, String country, String city) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.country = country;
        this.city = city;
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

    public LocalTime getSunrise() {
        return sunrise;
    }

    public void setSunrise(LocalTime sunrise) {
        this.sunrise = sunrise;
    }

    public LocalTime getSunset() {
        return sunset;
    }

    public void setSunset(LocalTime sunset) {
        this.sunset = sunset;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
