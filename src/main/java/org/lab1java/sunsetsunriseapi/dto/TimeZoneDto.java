package org.lab1java.sunsetsunriseapi.dto;

public class TimeZoneDto {
    private String timeZone;

    public TimeZoneDto(String timeZone) {
        this.timeZone = timeZone;
    }

    public TimeZoneDto() {
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
