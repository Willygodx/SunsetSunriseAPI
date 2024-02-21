package org.lab1java.sunsetsunriseapi.model;

import java.time.LocalTime;

public class SunInfoResponse {
    private LocalTime sunrise;
    private LocalTime sunset;

    public SunInfoResponse(LocalTime sunrise, LocalTime sunset) {
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public SunInfoResponse() {
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
