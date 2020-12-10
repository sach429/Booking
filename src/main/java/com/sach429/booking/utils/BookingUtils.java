package com.sach429.booking.utils;

import com.sach429.booking.service.BookingPersistenceService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingUtils {
    public static LocalDate convertStringToLocalDate(String stringDate) {
        return LocalDate.parse(stringDate, DateTimeFormatter.ofPattern(BookingPersistenceService.YYYY_MM_DD));
    }
}
