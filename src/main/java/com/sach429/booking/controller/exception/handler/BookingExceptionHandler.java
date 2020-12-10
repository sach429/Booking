package com.sach429.booking.controller.exception.handler;

import com.sach429.booking.controller.BookingController;
import com.sach429.booking.exception.BookingCreationException;
import com.sach429.booking.exception.BookingModifyException;
import com.sach429.booking.exception.BookingNotFoundException;
import com.sach429.booking.types.BookingError;
import com.sach429.booking.types.ErrorType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

@RestControllerAdvice
public class BookingExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BookingCreationException.class)
    public ResponseEntity<BookingError> handleBookingCreationException(BookingCreationException bookingCreationException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingCreationException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<BookingError>(bookingError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BookingModifyException.class)
    public ResponseEntity<BookingError> handleBookingModifyException(BookingModifyException bookingCreationException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingCreationException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<BookingError>(bookingError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        bookingError.setErrors(new ArrayList<>());
        Optional.of(ex.getBindingResult())
                .map(BindingResult::getFieldErrors)
                .orElseGet(ArrayList::new)
                .stream()
                .filter(Objects::nonNull)
                .forEach(fieldError -> {
                    ErrorType errorType = new ErrorType();
                    errorType.setDescription(fieldError.getField() + " " + fieldError.getDefaultMessage());
                    bookingError.getErrors().add(errorType);
                });
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<BookingError> handleBookingNotFoundException(BookingNotFoundException bookingCreationException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingCreationException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<BookingError>(bookingError, HttpStatus.NOT_FOUND);
    }
}
