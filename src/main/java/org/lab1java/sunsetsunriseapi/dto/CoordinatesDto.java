package org.lab1java.sunsetsunriseapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime sunrise;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime sunset;

    private String timeZone;

    private String country;

    private String city;
}
