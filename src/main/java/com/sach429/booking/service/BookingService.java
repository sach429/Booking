package com.sach429.booking.service;

import com.mongodb.DuplicateKeyException;
import com.sach429.booking.exception.BookingCreationException;
import com.sach429.booking.exception.BookingDateNotAvailableException;
import com.sach429.booking.exception.BookingModifyException;
import com.sach429.booking.exception.BookingNotFoundException;
import com.sach429.booking.model.Booking;
import com.sach429.booking.types.BookingCreate;
import com.sach429.booking.types.BookingModify;
import com.sach429.booking.utils.BookingUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingPersistenceService bookingPersistenceService;
    private final BookingValidationService bookingValidationService;

    public Booking getBooking(Long bookingId) throws BookingNotFoundException {
        return Optional.ofNullable(bookingPersistenceService.getBooking(bookingId))
                .orElseThrow(() -> new BookingNotFoundException("No matching booking Id: " + bookingId));
    }

    public List<Booking> getBookings(String email, String fromDate, String toDate, Booking.BookingStatus status) {
        return Optional.ofNullable(bookingPersistenceService.getBookings(new Booking(null, email, BookingUtils.convertStringToLocalDate(fromDate), BookingUtils.convertStringToLocalDate(toDate), status)))
                .orElseGet(ArrayList::new);
    }

    public Booking createBooking(BookingCreate bookingCreate) throws BookingCreationException {
        try {
            bookingValidationService.validateBookingRequestIsValid(bookingCreate);
            return bookingPersistenceService.createBooking(bookingCreate);
        } catch (DuplicateKeyException e) {
            throw new BookingCreationException(new BookingDateNotAvailableException("Booking dates not available"));
        } catch (Exception e) {
            throw new BookingCreationException(e);
        }
    }

    public Booking modifyBooking(BookingModify bookingModify, Long bookingId) throws BookingModifyException {
        try {
            bookingValidationService.validateBookingRequestIsValid(bookingModify, bookingId);
            if (bookingModify.getAction() == BookingModify.ActionType.MODIFY)
                return bookingPersistenceService.updateBooking(bookingModify, bookingId);
            else if (bookingModify.getAction() == BookingModify.ActionType.CANCEL)
                return bookingPersistenceService.cancelBooking(bookingModify, bookingId);
            else return null;
        } catch (DuplicateKeyException e) {
            throw new BookingModifyException(new BookingDateNotAvailableException("Booking dates not available"));
        } catch (Exception e) {
            throw new BookingModifyException(e);
        }
    }

    public String getTransactionId() {
        return bookingPersistenceService.getTransactionId();
    }
}
