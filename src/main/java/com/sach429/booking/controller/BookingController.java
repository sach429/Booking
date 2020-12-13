package com.sach429.booking.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sach429.booking.exception.BookingCreationException;
import com.sach429.booking.exception.BookingModifyException;
import com.sach429.booking.exception.BookingNotFoundException;
import com.sach429.booking.model.Booking;
import com.sach429.booking.service.BookingService;
import com.sach429.booking.types.BookingCreate;
import com.sach429.booking.types.BookingModify;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
public class BookingController {

    public static final ThreadLocal<String> transactionId = new ThreadLocal<>();

    private final BookingService bookingService;

    @GetMapping(path = "/bookings", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Booking> getAllBookings(@Nullable @RequestParam("email") String email, @Nullable @RequestParam("fromDate") String fromDate, @Nullable @RequestParam("toDate") String toDate, @Nullable @RequestParam("status") Booking.BookingStatus status) throws BookingNotFoundException {
        try {
            BookingController.transactionId.set(bookingService.getTransactionId());
            return bookingService.getBookings(email, fromDate, toDate, status);
        } catch (Exception e) {
            throw log.throwing(new BookingNotFoundException(e));
        }
    }

    @GetMapping(path = "/bookings/{bookingId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Booking getBooking(@Nullable @PathVariable("bookingId") Long bookingId) throws BookingNotFoundException {
        try {
            BookingController.transactionId.set(bookingService.getTransactionId());
            return bookingService.getBooking(bookingId);
        } catch (BookingNotFoundException e) {
            throw log.throwing(new BookingNotFoundException(e));
        }
    }

    @PostMapping(path = "/bookings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Booking createBooking(@Validated @RequestBody BookingCreate bookingCreate) throws BookingCreationException {
        try {
            BookingController.transactionId.set(bookingService.getTransactionId());
            return bookingService.createBooking(bookingCreate);
        } catch (Exception e) {
            throw log.throwing(new BookingCreationException(e));
        }
    }

    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    static class TestObject {
        @JsonProperty
        public List<LinkObject> links;

        @Data
        public static class LinkObject {
            private List<String> rel;
            private String href;
        }
    }

    @PutMapping(path = "/bookings/{bookingId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Booking modifyBooking(@Validated @RequestBody BookingModify bookingModify, @NonNull @PathVariable("bookingId") Long bookingId) throws BookingModifyException {
        try {
            BookingController.transactionId.set(bookingService.getTransactionId());
            return bookingService.modifyBooking(bookingModify, bookingId);
        } catch (Exception e) {
            throw log.throwing(new BookingModifyException(e));
        }
    }

}
