package com.sach429.booking.service;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookingIdGenerationService {
    private final MongoTemplate mongoTemplate;
    public static final String BOOKING_ID = "bookingId";

    public Long generateBookingId() {
        Update update = new Update();
        update.inc(BOOKING_ID);
        return mongoTemplate.findAndModify(new Query(), update, FindAndModifyOptions.options().returnNew(true).upsert(true), Document.class, "bookingSequence").get(BOOKING_ID, Long.class);
    }
}
