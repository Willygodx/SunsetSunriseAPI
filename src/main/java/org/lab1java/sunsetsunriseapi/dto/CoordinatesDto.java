package org.lab1java.sunsetsunriseapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CoordinatesDto {
    private double latitude;

    private double longitude;

    private LocalDate date;

    private LocalTime sunrise;

    private LocalTime sunset;

    private String timeZone;

    private String country;

    private String city;
}
