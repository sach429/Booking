package com.sach429.booking.exception;

public class BookingCreationException extends Exception {
    public BookingCreationException(String message) {
        super(message);
    }

    public BookingCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingCreationException(Throwable cause) {
        super(cause);
    }
}
