package com.sach429.booking.exception;

public class BookingNotFoundException extends Exception {
    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingNotFoundException(Throwable cause) {
        super(cause);
    }
}
