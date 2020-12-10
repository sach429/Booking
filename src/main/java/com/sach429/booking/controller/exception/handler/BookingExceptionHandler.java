package com.sach429.booking.controller.exception.handler;

import com.sach429.booking.controller.BookingController;
import com.sach429.booking.exception.*;
import com.sach429.booking.types.BookingError;
import com.sach429.booking.types.ErrorType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.*;

@RestControllerAdvice
public class BookingExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BookingCreationException.class)
    public ResponseEntity<BookingError> handleBookingCreationException(BookingCreationException bookingCreationException) {
        BinaryExceptionClassifier binaryExceptionClassifier = new BinaryExceptionClassifier(Arrays.asList(BookingValidationException.class));
        binaryExceptionClassifier.setTraverseCauses(true);
        if (binaryExceptionClassifier.classify(bookingCreationException)) {
            return handleBookingValidationException(BookingValidationException.class.cast(ExceptionUtils.getRootCause(bookingCreationException)));
        }
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingCreationException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<BookingError> handleBookingValidationException(BookingValidationException bookingValidationException) {
        BinaryExceptionClassifier binaryExceptionClassifier = new BinaryExceptionClassifier(Arrays.asList(BookingDateNotAvailableException.class));
        binaryExceptionClassifier.setTraverseCauses(true);
        if (binaryExceptionClassifier.classify(bookingValidationException)) {
            return handleBookingDateNotAvailable((BookingDateNotAvailableException) ExceptionUtils.getRootCause(bookingValidationException));
        }
        binaryExceptionClassifier = new BinaryExceptionClassifier(Collections.singletonList(BookingDatesInvalidException.class));
        binaryExceptionClassifier.setTraverseCauses(true);
        if (binaryExceptionClassifier.classify(bookingValidationException)) {
            return handleBookingDatesInvalid((BookingDatesInvalidException) ExceptionUtils.getRootCause(bookingValidationException));
        }
        binaryExceptionClassifier = new BinaryExceptionClassifier(Collections.singletonList(BookingNotFoundException.class));
        binaryExceptionClassifier.setTraverseCauses(true);
        if (binaryExceptionClassifier.classify(bookingValidationException)) {
            return handleBookingNotFoundException((BookingNotFoundException) ExceptionUtils.getRootCause(bookingValidationException));
        }
        binaryExceptionClassifier = new BinaryExceptionClassifier(Collections.singletonList(BookingAlreadyCancelledException.class));
        binaryExceptionClassifier.setTraverseCauses(true);
        if (binaryExceptionClassifier.classify(bookingValidationException)) {
            return handleBookingAlreadyCancelled((BookingAlreadyCancelledException) ExceptionUtils.getRootCause(bookingValidationException));
        }
        binaryExceptionClassifier = new BinaryExceptionClassifier(Collections.singletonList(BookingAlreadyInProgressException.class));
        binaryExceptionClassifier.setTraverseCauses(true);
        if (binaryExceptionClassifier.classify(bookingValidationException)) {
            return handleBookingAlreadyInProgress((BookingAlreadyInProgressException) ExceptionUtils.getRootCause(bookingValidationException));
        }
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingValidationException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<BookingError> handleBookingAlreadyInProgress(BookingAlreadyInProgressException bookingAlreadyInProgressException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingAlreadyInProgressException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<BookingError> handleBookingAlreadyCancelled(BookingAlreadyCancelledException bookingAlreadyCancelledException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingAlreadyCancelledException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<BookingError> handleBookingDatesInvalid(BookingDatesInvalidException bookingDatesInvalidException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingDatesInvalidException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);

    }

    private ResponseEntity<BookingError> handleBookingDateNotAvailable(BookingDateNotAvailableException bookingDateNotAvailableException) {
        BookingError bookingError = new BookingError();
        bookingError.setTransactionId(BookingController.transactionId.get());
        ErrorType errorType = new ErrorType();
        errorType.setDescription(bookingDateNotAvailableException.getMessage());
        bookingError.setErrors(Collections.singletonList(errorType));
        return new ResponseEntity<>(bookingError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingModifyException.class)
    public ResponseEntity<BookingError> handleBookingModifyException(BookingModifyException bookingModifyException) {
        BinaryExceptionClassifier binaryExceptionClassifier = new BinaryExceptionClassifier(Arrays.asList(BookingValidationException.class));
        binaryExceptionClassifier.setTraverseCauses(true);
        if (binaryExceptionClassifier.classify(bookingModifyException)) {
            return handleBookingValidationException(BookingValidationException.class.cast(ExceptionUtils.getRootCause(bookingModifyException)));
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
        return new ResponseEntity<BookingError>(bookingError, HttpStatus.NOT_FOUND);
    }
}
