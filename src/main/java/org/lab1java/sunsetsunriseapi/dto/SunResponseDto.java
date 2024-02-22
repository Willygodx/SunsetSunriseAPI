package org.lab1java.sunsetsunriseapi.dto;

import java.time.LocalTime;

public class SunResponseDto {
    private LocalTime sunrise;
    private LocalTime sunset;

    public SunResponseDto(LocalTime sunrise, LocalTime sunset) {
        this.sunrise = sunrise;
        this.sunset = sunset;
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
