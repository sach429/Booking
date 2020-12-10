package com.sach429.booking.exception;

public class BookingDatesInvalidException extends Exception {
    public BookingDatesInvalidException(String message) {
        super(message);
    }

    public BookingDatesInvalidException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingDatesInvalidException(Throwable cause) {
        super(cause);
    }
}
