package ru.ds.education.currency.exceptions;

import javax.jms.JMSException;

public class ApiServiceCbrError extends JMSException {
    public ApiServiceCbrError(String message) {
        super(message);
    }
}
