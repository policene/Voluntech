package com.policene.voluntech.exceptions;

public class VolunteerAlreadySubscribedException extends RuntimeException {
    public VolunteerAlreadySubscribedException(String message) {
        super(message);
    }
}
