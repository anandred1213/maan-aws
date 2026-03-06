package com.practise.revision.exceptionHandling.exceptions;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException(String string) {
        super(string);
    }
}
