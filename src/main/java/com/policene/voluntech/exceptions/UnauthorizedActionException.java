package com.policene.voluntech.exceptions;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException(String message) {
        super(message);
    }
}
