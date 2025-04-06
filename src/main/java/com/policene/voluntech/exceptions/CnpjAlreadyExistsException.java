package com.policene.voluntech.exceptions;

public class CnpjAlreadyExistsException extends RuntimeException {
    public CnpjAlreadyExistsException(String message) {
        super(message);
    }
}
