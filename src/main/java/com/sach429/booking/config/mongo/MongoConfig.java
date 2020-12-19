package com.sach429.booking.config.mongo;

import org.bson.BsonTimestamp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

@Configuration
public class MongoConfig {

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Collections.singletonList(new DateTimeReadConverter()));
    }

    static class DateTimeReadConverter implements Converter<BsonTimestamp, LocalDateTime> {

        @Override
        public LocalDateTime convert(BsonTimestamp bsonTimestamp) {
            Instant instant = Instant.ofEpochMilli(bsonTimestamp.getValue());
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        }
    }
}
