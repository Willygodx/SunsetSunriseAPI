package org.lab1java.sunsetsunriseapi.dto;

import java.time.LocalTime;

public class SunResponseDto {
    private LocalTime sunrise;
    private LocalTime sunset;
    private String timeZone;
    private String country;
    private String city;

    public SunResponseDto(LocalTime sunrise, LocalTime sunset, String timeZone, String country, String city) {
        this.sunrise = sunrise;
        this.sunset = sunset;
        this.timeZone = timeZone;
        this.country = country;
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public SunResponseDto(LocalTime sunrise, LocalTime sunset) {
        this.sunrise = sunrise;
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

    public SunResponseDto() {
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
}
