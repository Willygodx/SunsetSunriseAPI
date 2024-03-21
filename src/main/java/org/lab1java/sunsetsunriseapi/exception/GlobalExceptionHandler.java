package org.lab1java.sunsetsunriseapi.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.ConstraintViolationException;
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

import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({HttpClientErrorException.class, HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class, MissingServletRequestParameterException.class,
            ConstraintViolationException.class, JsonProcessingException.class, BadRequestErrorException.class,
            DateTimeParseException.class, IllegalArgumentException.class})
    public ResponseEntity<ExceptionMessage> handleBadRequestErrorException(Exception exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<>(new ExceptionMessage(HttpStatus.BAD_REQUEST.value(), exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<ExceptionMessage> handleInternalServerErrorException(RuntimeException exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<>(new ExceptionMessage(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<ExceptionMessage> handleResourceNotFoundException(RuntimeException exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<>(new ExceptionMessage(HttpStatus.NOT_FOUND.value(), exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionMessage> handleNoHandlerFoundException(NoHandlerFoundException exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<>(new ExceptionMessage(HttpStatus.NOT_FOUND.value(), exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionMessage> handleMethodNotAllowed(Exception exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<>(new ExceptionMessage(HttpStatus.METHOD_NOT_ALLOWED.value(), exception.getMessage()), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionMessage> handleMethodArgumentTypeMismatchException(Exception exception) {
        logger.error(exception.getMessage());
        return new ResponseEntity<>(new ExceptionMessage(HttpStatus.BAD_REQUEST.value(), "Invalid input!"), HttpStatus.BAD_REQUEST);
    }
}
