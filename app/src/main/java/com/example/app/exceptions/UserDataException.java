package com.example.app.exceptions;

public class UserDataException extends RuntimeException{
    public UserDataException(String message) {
        super(message);
    }

    public UserDataException(){

    }
}
