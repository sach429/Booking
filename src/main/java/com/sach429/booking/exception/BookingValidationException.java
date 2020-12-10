package com.sach429.booking.exception;

public class BookingValidationException extends Exception {
    public BookingValidationException(String message) {
        super(message);
    }

    public BookingValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingValidationException(Throwable cause) {
        super(cause);
    }
}
