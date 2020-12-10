package com.sach429.booking.exception;

public class BookingAlreadyInProgressException extends Exception {
    public BookingAlreadyInProgressException(String message) {
        super(message);
    }

    public BookingAlreadyInProgressException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingAlreadyInProgressException(Throwable cause) {
        super(cause);
    }
}
