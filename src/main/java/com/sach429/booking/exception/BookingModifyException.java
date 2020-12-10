package com.sach429.booking.exception;

public class BookingModifyException extends Exception {
    public BookingModifyException(String message) {
        super(message);
    }

    public BookingModifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingModifyException(Throwable cause) {
        super(cause);
    }
}
