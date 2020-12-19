package com.sach429.booking.service;

import com.sach429.booking.exception.*;
import com.sach429.booking.model.Booking;
import com.sach429.booking.properties.BookingConfigurationProperties;
import com.sach429.booking.types.BookingCreate;
import com.sach429.booking.types.BookingModify;
import com.sach429.booking.utils.BookingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    public void validateBookingIsConfirmedAndNotInProgress(Long bookingId) throws BookingAlreadyCancelledException, BookingNotFoundException, BookingAlreadyInProgressException {
        Booking booking = bookingPersistenceService.getBooking(bookingId);
        Optional.ofNullable(booking)
                .map(Booking::getBookingStatus)
                .map(Booking.BookingStatus.CONFIRMED::equals)
                .orElseThrow(() -> new BookingAlreadyCancelledException("Only confirmed booking can be modified"));
        Optional.of(booking)
                .map(Booking::getFromDate)
                .filter(LocalDate.now()::isBefore)
                .orElseThrow(() -> new BookingAlreadyInProgressException("Booking is already in progress and cannot be modified"));
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
            fromLocalDate = Optional.ofNullable(fromDate).map(BookingUtils::convertStringToLocalDate).orElseThrow(RuntimeException::new);
        } catch (Exception e) {
            throw new BookingDatesInvalidException("FromDate format is not valid");
        }
        try {
            toLocalDate = Optional.ofNullable(toDate).map(BookingUtils::convertStringToLocalDate).orElseThrow(RuntimeException::new);
        } catch (Exception e) {
            throw new BookingDatesInvalidException("ToDate format is not valid");
        }
//        check if fromDate is before toDate and number of days between is less than max duration
//        check if fromDate is between minDaysInAdvance and maxDaysInAdvance
        Optional.ofNullable(fromLocalDate)
                .filter(date -> date.isBefore(toLocalDate) || date.isEqual(toLocalDate))
                .filter(date -> date.until(toLocalDate).getDays() <= bookingConfigurationProperties.getMaxDuration() - 1)
                .filter(date -> date.isAfter(LocalDate.now().plusDays(bookingConfigurationProperties.getMinDaysInAdvance())) || date.isEqual(LocalDate.now().plusDays(bookingConfigurationProperties.getMinDaysInAdvance())))
                .filter(date -> date.isBefore(LocalDate.now().plusDays(bookingConfigurationProperties.getMaxDaysInAdvance())) || date.isEqual(LocalDate.now().plusDays(bookingConfigurationProperties.getMaxDaysInAdvance())))
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
                validateBookingDatesAreValid(bookingModify.getFromDate(), bookingModify.getToDate());
            }
            validateBookingIsConfirmedAndNotInProgress(bookingId);
        } catch (Exception e) {
            throw new BookingValidationException(e);
        }
    }
}
