package org.lab1java.sunsetsunriseapi.exception;

public class ApiNotFoundException extends RuntimeException{
    public ApiNotFoundException(String message) {
        super(message);
    }
}
