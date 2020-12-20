package com.sach429.booking.service;

import com.sach429.booking.exception.BookingCreationException;
import com.sach429.booking.exception.BookingModifyException;
import com.sach429.booking.exception.BookingNotFoundException;
import com.sach429.booking.exception.BookingValidationException;
import com.sach429.booking.model.Booking;
import com.sach429.booking.types.BookingCreate;
import com.sach429.booking.types.BookingModify;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.dao.DuplicateKeyException;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookingServiceTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    public BookingService bookingService;

    @Mock
    public BookingPersistenceService bookingPersistenceService;

    @Mock
    public BookingValidationService bookingValidationService;

    @Test
    public void testGetBookingWhenNotFound() throws BookingNotFoundException {
        when(bookingPersistenceService.getBooking(anyLong())).thenThrow(new BookingNotFoundException("Booking not found"));
        assertThatThrownBy(() -> bookingService.getBooking(1l)).isInstanceOf(BookingNotFoundException.class);

    }

    @Test
    public void testGetBookingWhenFound() throws BookingNotFoundException {
        Booking booking = mock(Booking.class);
        when(bookingPersistenceService.getBooking(anyLong())).thenReturn(booking);
        assertThat(bookingService.getBooking(1l)).isEqualTo(booking);
    }

    @Test
    public void testGetBookingsWhenInvalidFromDate() {
        assertThat(bookingService.getBookings("email", "2020-12-33", "2020-12-10", Booking.BookingStatus.CONFIRMED).size()).isEqualTo(0);
        verify(bookingPersistenceService, atMost(0)).getBookings(any(Booking.class));
    }

    @Test
    public void testGetBookingsWhenInvalidToDate() {
        assertThat(bookingService.getBookings("email", "2020-12-10", "2020-12-33", Booking.BookingStatus.CONFIRMED).size()).isEqualTo(0);
        verify(bookingPersistenceService, atMost(0)).getBookings(any(Booking.class));
    }

    @Test
    public void testGetBookingsWhenNullReturned() {
        ArgumentCaptor<Booking> bookingArgumentCaptor = ArgumentCaptor.forClass(Booking.class);
        Booking booking = mock(Booking.class);
        when(bookingPersistenceService.getBookings(any(Booking.class))).thenReturn(null);
        assertThat(bookingService.getBookings("email", "2020-12-10", "2020-12-12", Booking.BookingStatus.CONFIRMED).size()).isEqualTo(0);
        verify(bookingPersistenceService, atMost(1)).getBookings(bookingArgumentCaptor.capture());
        assertThat(bookingArgumentCaptor.getValue())
                .extracting("email", "fromDate", "toDate", "bookingStatus")
                .containsExactly("email", LocalDate.of(2020, 12, 10), LocalDate.of(2020, 12, 12), Booking.BookingStatus.CONFIRMED);
    }

    @Test
    public void testGetBookingsWhenNonNullReturned() {
        ArgumentCaptor<Booking> bookingArgumentCaptor = ArgumentCaptor.forClass(Booking.class);
        Booking booking = mock(Booking.class);
        when(bookingPersistenceService.getBookings(any(Booking.class))).thenReturn(Collections.singletonList(booking));
        assertThat(bookingService.getBookings("email", "2020-12-10", "2020-12-12", Booking.BookingStatus.CONFIRMED).size()).isEqualTo(1);
        verify(bookingPersistenceService, atMost(1)).getBookings(bookingArgumentCaptor.capture());
        assertThat(bookingArgumentCaptor.getValue())
                .extracting("email", "fromDate", "toDate", "bookingStatus")
                .containsExactly("email", LocalDate.of(2020, 12, 10), LocalDate.of(2020, 12, 12), Booking.BookingStatus.CONFIRMED);
    }

    @Test
    public void testModifyBookingWhenInvalidRequest() throws BookingValidationException {
        doThrow(new RuntimeException()).when(bookingValidationService).validateBookingRequestIsValid(any(BookingModify.class), anyLong());
        assertThatThrownBy(() -> bookingService.modifyBooking(mock(BookingModify.class), 1L)).isInstanceOf(BookingModifyException.class);
    }

    @Test
    public void testCreateBookingWhenInvalidRequest() throws BookingValidationException {
        doThrow(new RuntimeException()).when(bookingValidationService).validateBookingRequestIsValid(any(BookingCreate.class));
        assertThatThrownBy(() -> bookingService.createBooking(mock(BookingCreate.class))).isInstanceOf(BookingCreationException.class);
    }

    @Test
    public void testModifyBookingWhenDateNotAvailable() throws BookingValidationException {
        doThrow(new DuplicateKeyException("duplicate key")).when(bookingValidationService).validateBookingRequestIsValid(any(BookingModify.class), anyLong());
        assertThatThrownBy(() -> bookingService.modifyBooking(mock(BookingModify.class), 1L)).extracting("cause").extracting("message").isEqualTo("Booking dates not available");
    }

    @Test
    public void testCreateBookingWhenDateNotAvailable() throws BookingValidationException {
        doThrow(new DuplicateKeyException("duplicate key")).when(bookingValidationService).validateBookingRequestIsValid(any(BookingCreate.class));
        assertThatThrownBy(() -> bookingService.createBooking(mock(BookingCreate.class))).extracting("cause").extracting("message").isEqualTo("Booking dates not available");
    }

    @Test
    public void testModifyBooking() throws BookingValidationException, BookingModifyException {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.MODIFY);
        doNothing().when(bookingValidationService).validateBookingRequestIsValid(any(BookingModify.class), anyLong());
        Booking mockedBooking = mock(Booking.class);
        when(bookingPersistenceService.updateBooking(bookingModify, 1L)).thenReturn(mockedBooking);
        assertThat(bookingService.modifyBooking(bookingModify, 1L)).isEqualTo(mockedBooking);
        verify(bookingValidationService, atMostOnce()).validateBookingRequestIsValid(bookingModify, 1L);
        verify(bookingPersistenceService, atMostOnce()).updateBooking(bookingModify, 1L);
    }

    @Test
    public void testCancelBooking() throws BookingValidationException, BookingModifyException {
        BookingModify bookingModify = new BookingModify();
        bookingModify.setAction(BookingModify.ActionType.CANCEL);
        doNothing().when(bookingValidationService).validateBookingRequestIsValid(any(BookingModify.class), anyLong());
        Booking mockedBooking = mock(Booking.class);
        when(bookingPersistenceService.cancelBooking(bookingModify, 1L)).thenReturn(mockedBooking);
        assertThat(bookingService.modifyBooking(bookingModify, 1L)).isEqualTo(mockedBooking);
        verify(bookingValidationService, atMostOnce()).validateBookingRequestIsValid(bookingModify, 1L);
        verify(bookingPersistenceService, atMostOnce()).cancelBooking(bookingModify, 1L);
    }

    @Test
    public void testCreateBooking() throws BookingValidationException, BookingCreationException {
        BookingCreate bookingCreate = new BookingCreate();
        doNothing().when(bookingValidationService).validateBookingRequestIsValid(any(BookingModify.class), anyLong());
        Booking mockedBooking = mock(Booking.class);
        when(bookingPersistenceService.createBooking(bookingCreate)).thenReturn(mockedBooking);
        assertThat(bookingService.createBooking(bookingCreate)).isEqualTo(mockedBooking);
        verify(bookingValidationService, atMostOnce()).validateBookingRequestIsValid(bookingCreate);
        verify(bookingPersistenceService, atMostOnce()).createBooking(bookingCreate);
    }

    @Test
    public void testGetTransactionId() throws BookingValidationException, BookingModifyException {
        when(bookingPersistenceService.getTransactionId()).thenReturn("T1");
        assertThat(bookingService.getTransactionId()).isEqualTo("T1");
        verify(bookingPersistenceService, atMostOnce()).getTransactionId();
    }

}