package org.lab1java.sunsetsunriseapi.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
import java.time.format.DateTimeParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global exception handler to handle various types of exceptions.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
  private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handles exceptions related to bad requests.
   *
   * @param exception The exception to handle.
   * @return ResponseEntity containing the appropriate HTTP status code and error message.
   */
  @ExceptionHandler({HttpClientErrorException.class, HttpMessageNotReadableException.class,
      MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
      ConstraintViolationException.class, JsonProcessingException.class,
      BadRequestErrorException.class,
      DateTimeParseException.class, IllegalArgumentException.class, InvalidDataException.class,
      MethodArgumentTypeMismatchException.class,
  })
  public ResponseEntity<ExceptionMessage> handleBadRequestException(Exception exception) {
    logger.error(exception.getMessage());
    return new ResponseEntity<>(
        new ExceptionMessage(HttpStatus.BAD_REQUEST.value(), exception.getMessage()),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles internal server errors.
   *
   * @param exception The exception to handle.
   * @return ResponseEntity containing the appropriate HTTP status code and error message.
   */
  @ExceptionHandler({RuntimeException.class})
  public ResponseEntity<ExceptionMessage> handleInternalServerErrorException(
      RuntimeException exception) {
    logger.error(exception.getMessage());
    return new ResponseEntity<>(
        new ExceptionMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Handles resource not found exceptions.
   *
   * @param exception The exception to handle.
   * @return ResponseEntity containing the appropriate HTTP status code and error message.
   */
  @ExceptionHandler({ResourceNotFoundException.class, NoHandlerFoundException.class})
  public ResponseEntity<ExceptionMessage> handleNotFoundException(Exception exception) {
    logger.error(exception.getMessage());
    return new ResponseEntity<>(
        new ExceptionMessage(HttpStatus.NOT_FOUND.value(), exception.getMessage()),
        HttpStatus.NOT_FOUND);
  }

  /**
   * Handles method not allowed exceptions.
   *
   * @param exception The exception to handle.
   * @return ResponseEntity containing the appropriate HTTP status code and error message.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ExceptionMessage> handleMethodNotAllowed(Exception exception) {
    logger.error(exception.getMessage());
    return new ResponseEntity<>(
        new ExceptionMessage(HttpStatus.METHOD_NOT_ALLOWED.value(), exception.getMessage()),
        HttpStatus.METHOD_NOT_ALLOWED);
  }
}
