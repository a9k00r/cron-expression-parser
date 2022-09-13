package com.ankur.cronparser.exceptions;

public class CronInvalidFieldException extends RuntimeException {
    public CronInvalidFieldException(String message) {
        super(message);
    }
}
