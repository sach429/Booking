package com.sach429.booking.controller.exception.handler;

import com.sach429.booking.controller.BookingController;
import com.sach429.booking.exception.*;
import com.sach429.booking.types.BookingError;
import com.sach429.booking.types.ErrorType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        BookingValidationException rootCause = ExceptionUtils.throwableOfThrowable(bookingCreationException, BookingValidationException.class);
        if (rootCause != null) {
            return handleBookingValidationException(rootCause);
        }
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingCreationException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<BookingError> handleBookingValidationException(BookingValidationException bookingValidationException) {
        BookingDateNotAvailableException bookingDateNotAvailableException = ExceptionUtils.throwableOfThrowable(bookingValidationException, BookingDateNotAvailableException.class);
        if (bookingDateNotAvailableException != null) {
            return handleBookingDateNotAvailable(bookingDateNotAvailableException);
        }
        BookingDatesInvalidException bookingDatesInvalidException = ExceptionUtils.throwableOfThrowable(bookingValidationException, BookingDatesInvalidException.class);
        if (bookingDatesInvalidException != null) {
            return handleBookingDatesInvalid(bookingDatesInvalidException);
        }
        BookingNotFoundException BookingNotFoundException = ExceptionUtils.throwableOfThrowable(bookingValidationException, BookingNotFoundException.class);
        if (BookingNotFoundException != null) {
            return handleBookingNotFoundException(BookingNotFoundException);
        }
        BookingAlreadyCancelledException bookingAlreadyCancelledException = ExceptionUtils.throwableOfThrowable(bookingValidationException, BookingAlreadyCancelledException.class);
        if (bookingAlreadyCancelledException != null) {
            return handleBookingAlreadyCancelled(bookingAlreadyCancelledException);
        }
        BookingAlreadyInProgressException bookingAlreadyInProgressException = ExceptionUtils.throwableOfThrowable(bookingValidationException, BookingAlreadyInProgressException.class);
        if (bookingAlreadyInProgressException != null) {
            return handleBookingAlreadyInProgress(bookingAlreadyInProgressException);
        }
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingValidationException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingAlreadyInProgressException.class)
    public ResponseEntity<BookingError> handleBookingAlreadyInProgress(BookingAlreadyInProgressException bookingAlreadyInProgressException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingAlreadyInProgressException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingAlreadyCancelledException.class)
    public ResponseEntity<BookingError> handleBookingAlreadyCancelled(BookingAlreadyCancelledException bookingAlreadyCancelledException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingAlreadyCancelledException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingDatesInvalidException.class)
    public ResponseEntity<BookingError> handleBookingDatesInvalid(BookingDatesInvalidException bookingDatesInvalidException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingDatesInvalidException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(BookingDateNotAvailableException.class)
    public ResponseEntity<BookingError> handleBookingDateNotAvailable(BookingDateNotAvailableException bookingDateNotAvailableException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingDateNotAvailableException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingModifyException.class)
    public ResponseEntity<BookingError> handleBookingModifyException(BookingModifyException bookingModifyException) {
        BookingValidationException bookingValidationException = ExceptionUtils.throwableOfThrowable(bookingModifyException, BookingValidationException.class);
        if (bookingValidationException != null) {
            return handleBookingValidationException(bookingValidationException);
        }
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingModifyException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.INTERNAL_SERVER_ERROR);
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

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<BookingError> handleBookingNotFoundException(BookingNotFoundException bookingCreationException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingCreationException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.NOT_FOUND);
    }
}
