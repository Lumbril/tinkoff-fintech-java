package org.example.exceptions;

public class JsonException extends RuntimeException {
    public JsonException() {
        super();
    }

    public JsonException(String message) {
        super(message);
    }
}
