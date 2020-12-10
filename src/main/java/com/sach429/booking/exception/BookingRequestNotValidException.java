package com.sach429.booking.exception;

public class BookingRequestNotValidException extends Exception {
    public BookingRequestNotValidException(String message) {
        super(message);
    }

    public BookingRequestNotValidException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingRequestNotValidException(Throwable cause) {
        super(cause);
    }
}
