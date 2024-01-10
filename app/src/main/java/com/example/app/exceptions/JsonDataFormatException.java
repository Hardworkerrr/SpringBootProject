package com.example.app.exceptions;

public class JsonDataFormatException extends RuntimeException{
    public JsonDataFormatException(String message) {
        super(message);
    }

    public JsonDataFormatException() {

    }
}
