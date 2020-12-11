package com.sach429.booking.service;

import com.sach429.booking.exception.BookingNotFoundException;
import com.sach429.booking.model.Booking;
import com.sach429.booking.repository.BookingRepository;
import com.sach429.booking.types.BookingCreate;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class BookingPersistenceServiceTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    public BookingPersistenceService bookingPersistenceService;

    @Mock
    public BookingRepository bookingRepository;

    @Mock
    public MongoTemplate mongoTemplate;

    @Mock
    BookingIdGenerationService bookingIdGenerationService;

    @Test
    public void testRetrieveSingleBookingWhenNotFound() {
        when(bookingRepository.getBookingByBookingId(anyLong())).thenReturn(null);
        assertThatThrownBy(() -> bookingPersistenceService.getBooking(1l)).isInstanceOf(BookingNotFoundException.class);
    }

    @Test
    public void testRetrieveSingleBookingWhenFound() throws BookingNotFoundException {
        when(bookingRepository.getBookingByBookingId(anyLong())).thenReturn(mock(Booking.class));
        assertThat(bookingPersistenceService.getBooking(1l)).isInstanceOf(Booking.class);
    }

    @Test
    public void testRetrieveMultipleBookingsWhenFromDateInvalid() throws BookingNotFoundException {
        Booking booking = new Booking(null, null, null, "email", LocalDate.now(), LocalDate.now(), Booking.BookingStatus.CONFIRMED);
        bookingPersistenceService.getBookings(booking);
        verify(mongoTemplate, atMostOnce()).find(any(Query.class), eq(Booking.class));
    }

    @Test
    public void testCreateBooking() throws BookingNotFoundException {
        when(bookingIdGenerationService.generateBookingId()).thenReturn(1l);
        ArgumentCaptor<Booking> bookingArgumentCaptor = ArgumentCaptor.forClass(Booking.class);
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setEmail("email");
        bookingCreate.setFirstName("fname");
        bookingCreate.setLastName("lname");
        bookingCreate.setFromDate("2020-12-10");
        bookingCreate.setToDate("2020-12-12");
        bookingPersistenceService.createBooking(bookingCreate);
        verify(bookingRepository, atMostOnce()).save(bookingArgumentCaptor.capture());
        assertThat(bookingArgumentCaptor.getValue())
                .extracting("bookingId", "firstName", "lastName", "email", "fromDate", "toDate", "bookingStatus")
                .containsExactly(1l, "fname", "lname", "email", LocalDate.of(2020, 12, 10), LocalDate.of(2020, 12, 12), Booking.BookingStatus.CONFIRMED);
        assertThat(bookingArgumentCaptor.getValue().getDays()).containsExactlyInAnyOrder("2020-12-10", "2020-12-11", "2020-12-12");
    }
}