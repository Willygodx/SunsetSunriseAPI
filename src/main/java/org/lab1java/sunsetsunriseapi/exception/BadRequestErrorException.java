package org.lab1java.sunsetsunriseapi.exception;

public class BadRequestErrorException extends RuntimeException {
    public BadRequestErrorException(String message) {
        super(message);
    }
}
