package com.sach429.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sach429.booking.controller.exception.handler.BookingExceptionHandler;
import com.sach429.booking.exception.*;
import com.sach429.booking.model.Booking;
import com.sach429.booking.service.BookingService;
import com.sach429.booking.types.BookingCreate;
import com.sach429.booking.types.BookingModify;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookingControllerTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    public MockMvc mockMvc;

    @Mock
    public BookingService bookingService;

    @InjectMocks
    public BookingController bookingController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).setControllerAdvice(new BookingExceptionHandler()).build();
    }

    @Test
    public void testCreateBooking() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Booking booking = new Booking(1l, "fname", "lname", "email", LocalDate.of(2020, 12, 10), LocalDate.of(2020, 12, 12), Booking.BookingStatus.CONFIRMED);
        when(bookingService.createBooking(any(BookingCreate.class))).thenReturn(booking);
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setToDate("2020-12-20");
        bookingCreate.setFromDate("2020-12-18");
        bookingCreate.setEmail("email");
        bookingCreate.setLastName("lname");
        bookingCreate.setFirstName("fname");
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.bookingId").value(1l))
                .andExpect(jsonPath("$.firstName").value("fname"))
                .andExpect(jsonPath("$.lastName").value("lname"))
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));
    }

    @Test
    public void testCreateBookingWhenMissingEmailRequiredField() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setToDate("2020-12-20");
        bookingCreate.setFromDate("2020-12-18");
        bookingCreate.setLastName("lname");
        bookingCreate.setFirstName("fname");
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value("email must not be null"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenMissingFirstNameRequiredField() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setToDate("2020-12-20");
        bookingCreate.setFromDate("2020-12-18");
        bookingCreate.setLastName("lname");
        bookingCreate.setEmail("email");
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value("firstName must not be null"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenMissingLastNameRequiredField() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setToDate("2020-12-20");
        bookingCreate.setFromDate("2020-12-18");
        bookingCreate.setFirstName("fname");
        bookingCreate.setEmail("email");
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value("lastName must not be null"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenMissingFromDateRequiredField() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setToDate("2020-12-20");
        bookingCreate.setFirstName("fname");
        bookingCreate.setLastName("lname");
        bookingCreate.setEmail("email");
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value("fromDate must not be null"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenMissingFToDateRequiredField() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate("2020-12-20");
        bookingCreate.setFirstName("fname");
        bookingCreate.setLastName("lname");
        bookingCreate.setEmail("email");
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value("toDate must not be null"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenFromDateInvalidPattern() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate("20220-12-20");
        bookingCreate.setToDate("2020-12-20");
        bookingCreate.setFirstName("fname");
        bookingCreate.setLastName("lname");
        bookingCreate.setEmail("email");
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].description").value(Matchers.containsInAnyOrder(Matchers.containsString("fromDate must match"), Matchers.containsString("fromDate size must be between"))))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenToDateInvalidPattern() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate("2020-12-20");
        bookingCreate.setToDate("20260-12-20");
        bookingCreate.setFirstName("fname");
        bookingCreate.setLastName("lname");
        bookingCreate.setEmail("email");
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[*].description").value(Matchers.containsInAnyOrder(Matchers.containsString("toDate must match"), Matchers.containsString("toDate size must be between"))))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testModifyBookingWhenEncounteredException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingModify bookingModify = new BookingModify();
        bookingModify.setFromDate("2020-12-20");
        bookingModify.setToDate("2026-12-20");
        when(bookingService.modifyBooking(any(BookingModify.class), anyLong())).thenThrow(new BookingModifyException("cannot modify booking"));
        mockMvc.perform(put("/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingModify)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errors[0].description").value(Matchers.containsStringIgnoringCase("cannot modify booking")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testModifyBookingWhenEncounteredBookingNotFoundException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingModify bookingModify = new BookingModify();
        bookingModify.setFromDate("2020-12-20");
        bookingModify.setToDate("2026-12-20");
        when(bookingService.modifyBooking(any(BookingModify.class), anyLong())).thenThrow(new BookingModifyException(new BookingValidationException(new BookingNotFoundException("booking not found"))));
        mockMvc.perform(put("/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingModify)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].description").value(Matchers.containsStringIgnoringCase("booking not found")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testModifyBookingWhenEncounteredDatesNotAvailableException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingModify bookingModify = new BookingModify();
        bookingModify.setFromDate("2020-12-20");
        bookingModify.setToDate("2026-12-20");
        when(bookingService.modifyBooking(any(BookingModify.class), anyLong())).thenThrow(new BookingModifyException(new BookingValidationException(new BookingDateNotAvailableException("booking dates not available"))));
        mockMvc.perform(put("/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingModify)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value(Matchers.containsStringIgnoringCase("booking dates not available")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testModifyBookingWhenEncounteredDatesInvalidException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingModify bookingModify = new BookingModify();
        bookingModify.setFromDate("2020-12-22");
        bookingModify.setToDate("2026-12-20");
        when(bookingService.modifyBooking(any(BookingModify.class), anyLong())).thenThrow(new BookingModifyException(new BookingValidationException(new BookingDatesInvalidException("booking dates not in range"))));
        mockMvc.perform(put("/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingModify)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value(Matchers.containsStringIgnoringCase("booking dates not in range")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenEncounteredException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate("2020-12-20");
        bookingCreate.setToDate("2026-12-20");
        bookingCreate.setFirstName("fname");
        bookingCreate.setLastName("lname");
        bookingCreate.setEmail("email");
        when(bookingService.createBooking(any(BookingCreate.class))).thenThrow(new BookingCreationException("cannot create booking"));
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errors[0].description").value(Matchers.containsStringIgnoringCase("cannot create booking")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenEncounteredValidationException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate("2020-12-20");
        bookingCreate.setToDate("2026-12-20");
        bookingCreate.setFirstName("fname");
        bookingCreate.setLastName("lname");
        bookingCreate.setEmail("email");
        when(bookingService.createBooking(any(BookingCreate.class))).thenThrow(new BookingCreationException(new BookingValidationException("booking not valid")));
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value(Matchers.containsStringIgnoringCase("booking not valid")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenEncounteredDatesNotAvailableException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate("2020-12-20");
        bookingCreate.setToDate("2026-12-20");
        bookingCreate.setFirstName("fname");
        bookingCreate.setLastName("lname");
        bookingCreate.setEmail("email");
        when(bookingService.createBooking(any(BookingCreate.class))).thenThrow(new BookingCreationException(new BookingValidationException(new BookingDateNotAvailableException("booking dates not available"))));
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value(Matchers.containsStringIgnoringCase("booking dates not available")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testCreateBookingWhenEncounteredDatesInvalidException() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        BookingCreate bookingCreate = new BookingCreate();
        bookingCreate.setFromDate("2020-12-22");
        bookingCreate.setToDate("2026-12-20");
        bookingCreate.setFirstName("fname");
        bookingCreate.setLastName("lname");
        bookingCreate.setEmail("email");
        when(bookingService.createBooking(any(BookingCreate.class))).thenThrow(new BookingCreationException(new BookingValidationException(new BookingDatesInvalidException("booking dates not in range"))));
        mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingCreate)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].description").value(Matchers.containsStringIgnoringCase("booking dates not in range")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void testGetBookingWhenNotFound() throws Exception {
        when(bookingService.getBooking(anyLong())).thenThrow(new BookingNotFoundException("not found"));
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/1"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].description").value(Matchers.containsStringIgnoringCase("not found")))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE.toString(), MediaType.APPLICATION_JSON_VALUE));
    }

}