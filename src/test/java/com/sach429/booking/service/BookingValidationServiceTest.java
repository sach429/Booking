package com.sach429.booking.service;

import com.sach429.booking.exception.BookingNotFoundException;
import com.sach429.booking.model.Booking;
import com.sach429.booking.properties.BookingConfigurationProperties;
import com.sach429.booking.types.BookingCreate;
import com.sach429.booking.types.BookingModify;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BookingValidationServiceTest {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    public BookingValidationService bookingValidationService;

    @Mock
    public BookingPersistenceService bookingPersistenceService;

    @Mock
    public BookingConfigurationProperties bookingConfigurationProperties;

    @Before
    public void setup() {
        when(bookingConfigurationProperties.getMinDaysInAdvance()).thenReturn(1);
        when(bookingConfigurationProperties.getMaxDaysInAdvance()).thenReturn(30);
        when(bookingConfigurationProperties.getMaxDuration()).thenReturn(3);
    }

    @Test
    public void testWhenBookingCancelWhenMissingReason() {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.CANCEL);
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("Cancellation reason is required for cancel request");
    }

    @Test
    public void testWhenBookingUpdateWhenMissingFromDate() {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setFromDate("2020-12-10");
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("FromDate and ToDate are required for modify request");
    }

    @Test
    public void testWhenBookingUpdateWhenMissingToDate() {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setToDate("2020-12-10");
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("FromDate and ToDate are required for modify request");
    }

    @Test
    public void testWhenBookingUpdateFromDateNotProperFormat() {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setFromDate("20210-12-10");
        bookingModify.setToDate("20210-12-11");
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("FromDate format is not valid");
    }

    @Test
    public void testWhenBookingUpdateToDateNotProperFormat() {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setToDate("20210-12-10");
        bookingModify.setFromDate("2020-12-10");
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("ToDate format is not valid");
    }

    @Test
    public void testWhenBookingUpdateFromDateAfterToDate() {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setToDate("2020-12-10");
        bookingModify.setFromDate("2020-12-11");
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("Booking Dates not in range");
    }

    @Test
    public void testWhenBookingModifyTooEarly() {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setFromDate(LocalDate.now().minusDays(1L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        bookingModify.setToDate(LocalDate.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("Booking Dates not in range");
    }

    @Test
    public void testWhenBookingModifyTooLate() {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setFromDate(LocalDate.now().plusDays(31L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        bookingModify.setToDate(LocalDate.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("Booking Dates not in range");
    }

    @Test
    public void testWhenBookingModifyDurationTooLong() {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setFromDate(LocalDate.now().plusDays(2L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        bookingModify.setToDate(LocalDate.now().plusDays(5L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("Booking Dates not in range");
    }

    @Test
    public void testWhenBookingCreateFromDateNotProperFormat() {
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate("20210-12-10");
        bookingCreate.setToDate("20210-12-11");
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingCreate)).extracting("cause").extracting("message").isEqualTo("FromDate format is not valid");
    }

    @Test
    public void testWhenBookingCreateToDateNotProperFormat() {
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setToDate("20210-12-10");
        bookingCreate.setFromDate("2020-12-10");
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingCreate)).extracting("cause").extracting("message").isEqualTo("ToDate format is not valid");
    }

    @Test
    public void testWhenBookingCreateFromDateAfterToDate() {
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setToDate("2020-12-10");
        bookingCreate.setFromDate("2020-12-11");
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingCreate)).extracting("cause").extracting("message").isEqualTo("Booking Dates not in range");
    }

    @Test
    public void testWhenBookingCreateTooEarly() {
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate(LocalDate.now().minusDays(1L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        bookingCreate.setToDate(LocalDate.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingCreate)).extracting("cause").extracting("message").isEqualTo("Booking Dates not in range");
    }

    @Test
    public void testWhenBookingCreateTooLate() {
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate(LocalDate.now().plusDays(31L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        bookingCreate.setToDate(LocalDate.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingCreate)).extracting("cause").extracting("message").isEqualTo("Booking Dates not in range");
    }

    @Test
    public void testWhenBookingCreateDurationTooLong() {
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate(LocalDate.now().plusDays(2L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        bookingCreate.setToDate(LocalDate.now().plusDays(5L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingCreate)).extracting("cause").extracting("message").isEqualTo("Booking Dates not in range");
    }

    @Test
    public void testWhenModifyBookingAlreadyInProgress() throws BookingNotFoundException {
        Booking booking = new Booking(1L, "", "", "", LocalDate.now(), LocalDate.now(), Booking.BookingStatus.CONFIRMED);
        when(bookingPersistenceService.getBooking(anyLong())).thenReturn(booking);
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setFromDate(LocalDate.now().plusDays(2L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        bookingModify.setToDate(LocalDate.now().plusDays(4L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("Booking is already in progress and cannot be modified");
    }

    @Test
    public void testWhenModifyBookingAlreadyCancelled() throws BookingNotFoundException {
        Booking booking = new Booking(1L, "", "", "", LocalDate.now().plusDays(2L), LocalDate.now().plusDays(3L), Booking.BookingStatus.CANCELLED);
        when(bookingPersistenceService.getBooking(anyLong())).thenReturn(booking);
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        bookingModify.setFromDate(LocalDate.now().plusDays(2L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        bookingModify.setToDate(LocalDate.now().plusDays(4L).format(DateTimeFormatter.ofPattern(YYYY_MM_DD)));
        assertThatThrownBy(() -> bookingValidationService.validateBookingRequestIsValid(bookingModify, 1L)).extracting("cause").extracting("message").isEqualTo("Only confirmed booking can be modified");
    }

    @Test
    public void testWhenBookingNotFound() throws BookingNotFoundException {
        when(bookingPersistenceService.getBooking(anyLong())).thenReturn(null);
        assertThatThrownBy(() -> bookingValidationService.validateBookingExists(1L)).extracting("message").isEqualTo("BookingId: 1 is not found");
    }

}