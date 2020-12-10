package com.sach429.booking.service;

import com.sach429.booking.exception.BookingAlreadyCancelledException;
import com.sach429.booking.exception.BookingDatesInvalidException;
import com.sach429.booking.exception.BookingNotFoundException;
import com.sach429.booking.exception.BookingValidationException;
import com.sach429.booking.model.Booking;
import com.sach429.booking.properties.BookingConfigurationProperties;
import com.sach429.booking.types.BookingCreate;
import com.sach429.booking.types.BookingModify;
import com.sach429.booking.utils.BookingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jni.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingValidationService {
    private final BookingPersistenceService bookingPersistenceService;
    private final BookingConfigurationProperties bookingConfigurationProperties;

    public void validateBookingExists(Long bookingId) throws BookingNotFoundException {
        Optional.ofNullable(bookingPersistenceService.getBooking(bookingId))
                .orElseThrow(() -> new BookingNotFoundException("BookingId: " + bookingId + " is not found"));
    }

    public void validateBookingIsConfirmedAndNotInProgress(Long bookingId) throws BookingAlreadyCancelledException, BookingNotFoundException {
        Booking booking = bookingPersistenceService.getBooking(bookingId);
        Optional.ofNullable(booking)
                .map(Booking::getBookingStatus)
                .map(Booking.BookingStatus.CONFIRMED::equals)
                .orElseThrow(() -> new BookingAlreadyCancelledException("Only confirmed booking can be modified"));
        Optional.of(booking)
                .map(Booking::getFromDate)
                .filter(LocalDate.now()::isAfter)
                .orElseThrow(()->new BookingALreadyInProgressException("Booking is already in progress and cannot be modified"))
    }

    public void validateBookingRequestIsValid(BookingCreate bookingCreate) throws BookingValidationException {
        try {
            validateBookingDatesAreValid(bookingCreate.getFromDate(), bookingCreate.getToDate());
        } catch (Exception e) {
            throw new BookingValidationException(e);
        }
    }

    private void validateBookingDatesAreValid(String fromDate, String toDate) throws BookingDatesInvalidException {
        LocalDate toLocalDate, fromLocalDate;
//        check if fromDate and toDate are in required format of yyyy-MM-dd
        try {
            fromLocalDate = BookingUtils.convertStringToLocalDate(fromDate);
        } catch (Exception e) {
            throw new BookingDatesInvalidException("FromDate format is not valid");
        }
        try {
            toLocalDate = BookingUtils.convertStringToLocalDate(toDate);
        } catch (Exception e) {
            throw new BookingDatesInvalidException("ToDate format is not valid");
        }
//        check if fromDate is before toDate and number of days between is less than max duration
//        check if fromDate is between minDaysInAdvance and maxDaysInAdvance
        Optional.ofNullable(fromLocalDate)
                .filter(date -> date.isBefore(toLocalDate))
                .filter(date -> date.until(toLocalDate).getDays() <= bookingConfigurationProperties.getMaxDuration() - 1)
                .filter(date -> LocalDate.now().until(fromLocalDate).getDays() >= bookingConfigurationProperties.getMinDaysInAdvance())
                .filter(date -> fromLocalDate.plusDays(bookingConfigurationProperties.getMaxDaysInAdvance()).until(LocalDate.now().plusDays(bookingConfigurationProperties.getMaxDaysInAdvance())).getDays() <= 0)
                .orElseThrow(() -> new BookingDatesInvalidException("Booking Dates not in range"));
    }

    public void validateBookingRequestIsValid(BookingModify bookingModify, Long bookingId) throws BookingValidationException {
        try {
            if (bookingModify.getAction() == BookingModify.ActionType.CANCEL) {
                if (StringUtils.isBlank(bookingModify.getReason()))
                    throw new BookingValidationException("Cancellation reason is required for cancel request");
            }
            if (bookingModify.getAction() == BookingModify.ActionType.MODIFY) {
                if (StringUtils.isBlank(bookingModify.getFromDate()) || StringUtils.isBlank(bookingModify.getToDate()))
                    throw new BookingValidationException("FromDate and ToDate are required for modify request");
            }
            validateBookingDatesAreValid(bookingModify.getFromDate(), bookingModify.getToDate());
            validateBookingIsConfirmedAndNotInProgress(bookingId);
        } catch (Exception e) {
            throw new BookingValidationException(e);
        }
    }
}
