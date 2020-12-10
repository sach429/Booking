package com.sach429.booking;

import com.mongodb.BasicDBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import com.sach429.booking.model.Booking;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableMongoRepositories
public class BookingApplication {

    private final MongoTemplate mongoTemplate;

    BookingApplication(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class);
    }

    @PostConstruct
    public void initCollections() {
        MongoCollection<Document> collection = mongoTemplate.getCollection(mongoTemplate.getCollectionName(Booking.class));
        try {
            collection.createIndex(new BasicDBObject().append("days", 1).append("bookingStatus", 1), new IndexOptions().unique(true).partialFilterExpression(new BasicDBObject().append("bookingStatus", Booking.BookingStatus.CONFIRMED.name())));
            collection.createIndex(new BasicDBObject().append("bookingId", 1), new IndexOptions().unique(true));
            collection.createIndex(new BasicDBObject().append("email", 1));
            collection.createIndex(new BasicDBObject().append("bookingStatus", 1));
            collection.createIndex(new BasicDBObject().append("fromDate", 1).append("toDate", 1));
        } catch (DuplicateKeyException e) {
            // ignore
        }
    }
}