package org.lab1java.sunsetsunriseapi.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExceptionMessage {
    private int statusCode;

    private String message;
}
