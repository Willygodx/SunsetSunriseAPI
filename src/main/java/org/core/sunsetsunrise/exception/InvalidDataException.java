package org.core.sunsetsunrise.exception;

/**
 * Exception thrown when the provided data is invalid.
 */
public class InvalidDataException extends RuntimeException {
  public InvalidDataException(String message) {
    super(message);
  }
}
