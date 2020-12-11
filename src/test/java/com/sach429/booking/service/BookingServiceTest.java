package com.sach429.booking.service;

import com.sach429.booking.exception.BookingNotFoundException;
import com.sach429.booking.model.Booking;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

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


}