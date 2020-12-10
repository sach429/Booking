package com.sach429.booking.exception;

public class BookingALreadyInProgressException extends Exception {
    public BookingALreadyInProgressException(String message) {
        super(message);
    }

    public BookingALreadyInProgressException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingALreadyInProgressException(Throwable cause) {
        super(cause);
    }
}
