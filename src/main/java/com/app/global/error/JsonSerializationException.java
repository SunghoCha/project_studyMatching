package com.app.global.error;

public class JsonSerializationException extends RuntimeException {

    public JsonSerializationException(String message) {
        super(message);
    }

    public JsonSerializationException(Throwable cause) {
        super(cause);
    }

    public JsonSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
