package org.lab1java.sunsetsunriseapi.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
  @InjectMocks
  private GlobalExceptionHandler globalExceptionHandler;

  @Test
  void testHandleBadRequestException() {
    String errorMessage = "error";
    BadRequestErrorException exception = new BadRequestErrorException(errorMessage);

    ResponseEntity<ExceptionMessage> responseEntity =
        globalExceptionHandler.handleBadRequestException(exception);

    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  void testHandleInternalServerErrorException() {
    String errorMessage = "error";
    RuntimeException exception = new RuntimeException(errorMessage);

    ResponseEntity<ExceptionMessage> responseEntity =
        globalExceptionHandler.handleInternalServerErrorException(exception);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
  }

  @Test
  void testHandleMethodNotAllowed() {
    String errorMessage = "error";
    HttpRequestMethodNotSupportedException exception =
        new HttpRequestMethodNotSupportedException(errorMessage);

    ResponseEntity<ExceptionMessage> responseEntity =
        globalExceptionHandler.handleMethodNotAllowed(exception);

    assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
  }
}
