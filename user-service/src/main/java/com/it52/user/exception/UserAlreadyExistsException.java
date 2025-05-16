package com.it52.user.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String field, String value) {
        super("User with " + field + " '" + value + "' already exists.");
    }
}