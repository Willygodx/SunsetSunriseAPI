package org.lab1java.sunsetsunriseapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for Response.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseDto {
  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime sunrise;

  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime sunset;

  private String timeZone;

  private String country;

  private String city;

  public ResponseDto(LocalTime sunrise, LocalTime sunset) {
    this.sunrise = sunrise;
    this.sunset = sunset;
  }
}
