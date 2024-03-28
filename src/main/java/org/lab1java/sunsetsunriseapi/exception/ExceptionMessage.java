package org.lab1java.sunsetsunriseapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class representing a message for an exception.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExceptionMessage {
  private int statusCode;

  private String message;
}
