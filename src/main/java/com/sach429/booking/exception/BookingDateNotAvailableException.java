package com.sach429.booking.exception;

public class BookingDateNotAvailableException extends Exception {
    public BookingDateNotAvailableException(String message) {
        super(message);
    }

    public BookingDateNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingDateNotAvailableException(Throwable cause) {
        super(cause);
    }
}
