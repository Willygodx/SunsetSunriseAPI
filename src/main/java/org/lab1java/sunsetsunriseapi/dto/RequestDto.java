package org.lab1java.sunsetsunriseapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for Request.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestDto {
  private double latitude;

  private double longitude;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;
}
