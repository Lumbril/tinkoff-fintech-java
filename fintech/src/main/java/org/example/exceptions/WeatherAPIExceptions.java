package org.example.exceptions;

import lombok.Getter;

@Getter
public class WeatherAPIExceptions extends RuntimeException {
    private int errorCode;

    public WeatherAPIExceptions(int errorCode) {
        super();
        this.errorCode = errorCode;
    }

    public WeatherAPIExceptions(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
