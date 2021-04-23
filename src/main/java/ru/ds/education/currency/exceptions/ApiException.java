package ru.ds.education.currency.exceptions;

import java.time.ZonedDateTime;

public class ApiException {
    private final String message;
    private final ZonedDateTime timeStamp;

    public ApiException(String message, ZonedDateTime timeStamp) {
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }


    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }
}
