package ru.ds.education.currency.exceptions;

public class ApiBadData extends RuntimeException {
    public ApiBadData(String message) {
        super(message);
    }
}
