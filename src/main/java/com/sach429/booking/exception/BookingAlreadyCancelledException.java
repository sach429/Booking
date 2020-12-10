package com.sach429.booking.exception;

public class BookingAlreadyCancelledException extends Exception {
    public BookingAlreadyCancelledException(String message) {
        super(message);
    }

    public BookingAlreadyCancelledException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingAlreadyCancelledException(Throwable cause) {
        super(cause);
    }
}
