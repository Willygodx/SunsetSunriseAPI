package org.lab1java.sunsetsunriseapi.exception;

/**
 * Exception thrown when the provided data is invalid.
 */
public class InvalidDataException extends RuntimeException {
  public InvalidDataException(String message) {
    super(message);
  }
}
