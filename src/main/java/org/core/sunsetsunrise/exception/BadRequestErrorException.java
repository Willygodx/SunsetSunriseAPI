package org.core.sunsetsunrise.exception;

/**
 * Custom exception class representing a bad request error.
 */
public class BadRequestErrorException extends RuntimeException {
  public BadRequestErrorException(String message) {
    super(message);
  }
}
