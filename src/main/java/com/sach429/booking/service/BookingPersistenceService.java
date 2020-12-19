package com.sach429.booking.service;

import com.sach429.booking.exception.BookingNotFoundException;
import com.sach429.booking.model.Booking;
import com.sach429.booking.repository.BookingRepository;
import com.sach429.booking.types.BookingCreate;
import com.sach429.booking.types.BookingModify;
import com.sach429.booking.utils.BookingUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class BookingPersistenceService {
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FROM_DATE = "fromDate";
    public static final String TO_DATE = "toDate";
    public static final String DAYS = "days";
    public static final String BOOKING_STATUS = "bookingStatus";
    public static final String BOOKING_ID = "bookingId";
    public static final String EMAIL = "email";
    public static final String CANCELLATION_REASON = "cancellationReason";
    public static final String LAST_UPDATE_TIMESTAMP = "lastUpdateTimestamp";
    private final BookingRepository bookingRepository;
    private final MongoTemplate mongoTemplate;
    private final BookingIdGenerationService bookingIdGenerationService;

    public Booking getBooking(Long id) throws BookingNotFoundException {
        return Optional.ofNullable(bookingRepository.getBookingByBookingId(id)).orElseThrow(() -> new BookingNotFoundException("BookingId: " + id + " cannot be found"));
    }

    public List<Booking> getBookings(com.sach429.booking.model.Booking booking) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNoneBlank(booking.getEmail())) {
            criteria.andOperator(Criteria.where(EMAIL).is(booking.getEmail()));
        }
        if (booking.getBookingStatus() != null) {
            criteria.andOperator(Criteria.where(BOOKING_STATUS).is(booking.getBookingStatus()));
        }
        if (booking.getBookingStatus() != null) {
            criteria.andOperator(Criteria.where(FROM_DATE).gte(booking.getToDate()));
        }
        if (booking.getBookingStatus() != null) {
            criteria.andOperator(Criteria.where(TO_DATE).lte(booking.getFromDate()));
        }
        Query query = new Query();
        query.addCriteria(criteria);
        return mongoTemplate.find(query, Booking.class);
    }

    public Booking createBooking(BookingCreate bookingCreate) {
        Long bookingId = bookingIdGenerationService.generateBookingId();
        LocalDate fromDate = BookingUtils.convertStringToLocalDate(bookingCreate.getFromDate());
        LocalDate toDate = BookingUtils.convertStringToLocalDate(bookingCreate.getToDate());
        List<String> days = getListOfDaysBetweenFromAndToDate(fromDate, toDate);
        Booking booking = new Booking(bookingId, bookingCreate.getFirstName(), bookingCreate.getLastName(), bookingCreate.getEmail(), fromDate, toDate, Booking.BookingStatus.CONFIRMED);
        booking.setDays(days);
        booking.setLastUpdateTimestamp(LocalDateTime.now());
        bookingRepository.save(booking);
        return booking;
    }

    private List<String> getListOfDaysBetweenFromAndToDate(LocalDate fromDate, LocalDate toDate) {
        return IntStream.range(0, fromDate.until(toDate).getDays() + 1)
                .boxed()
                .map(fromDate::plusDays)
                .map(date -> date.format(DateTimeFormatter.ofPattern(YYYY_MM_DD)))
                .collect(Collectors.toList());
    }

    public Booking updateBooking(BookingModify bookingModify, Long bookingId) {
        LocalDate fromDate = BookingUtils.convertStringToLocalDate(bookingModify.getFromDate());
        LocalDate toDate = BookingUtils.convertStringToLocalDate(bookingModify.getToDate());
        Update update = new Update()
                .set(FROM_DATE, fromDate)
                .set(TO_DATE, toDate)
                .set(DAYS, getListOfDaysBetweenFromAndToDate(fromDate, toDate))
                .set(LAST_UPDATE_TIMESTAMP, LocalDateTime.now());
        Booking oldBooking = mongoTemplate.findAndModify(Query
                        .query(Criteria
                                .where(BOOKING_ID).
                                        is(bookingId).andOperator(Criteria.where(BOOKING_STATUS).is(Booking.BookingStatus.CONFIRMED))),
                update, FindAndModifyOptions.options().returnNew(false), Booking.class);
        Optional.ofNullable(oldBooking).ifPresent(b -> b.setChangeHistory(null));
        update = new Update().push("changeHistory", oldBooking);
        return mongoTemplate.findAndModify(Query
                        .query(Criteria
                                .where(BOOKING_ID).
                                        is(bookingId).andOperator(Criteria.where(BOOKING_STATUS).is(Booking.BookingStatus.CONFIRMED))),
                update, FindAndModifyOptions.options().returnNew(true), Booking.class);
    }

    public Booking cancelBooking(BookingModify bookingModify, Long bookingId) {
        Update update = new Update()
                .set(BOOKING_STATUS, Booking.BookingStatus.CANCELLED)
                .set(CANCELLATION_REASON, bookingModify.getReason())
                .set(LAST_UPDATE_TIMESTAMP, LocalDateTime.now());
        Booking oldBooking = mongoTemplate.findAndModify(Query
                        .query(Criteria
                                .where(BOOKING_ID).
                                        is(bookingId).andOperator(Criteria.where(BOOKING_STATUS).is(Booking.BookingStatus.CONFIRMED))),
                update, FindAndModifyOptions.options().returnNew(false), Booking.class);
        Optional.ofNullable(oldBooking).ifPresent(b -> b.setChangeHistory(null));
        update = new Update().push("changeHistory", oldBooking);
        return mongoTemplate.findAndModify(Query
                        .query(Criteria
                                .where(BOOKING_ID).
                                        is(bookingId).andOperator(Criteria.where(BOOKING_STATUS).is(Booking.BookingStatus.CANCELLED))),
                update, FindAndModifyOptions.options().returnNew(true), Booking.class);
    }

    public String getTransactionId() {
        Update update = new Update();
        update.inc(BOOKING_ID);
        return "T" + mongoTemplate.findAndModify(new Query(), update, FindAndModifyOptions.options().returnNew(true).upsert(true), Document.class, "transactionSequence").get(BOOKING_ID, Long.class);
    }
}
