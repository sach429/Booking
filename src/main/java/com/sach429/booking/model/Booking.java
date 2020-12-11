package com.sach429.booking.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "bookings")
@RequiredArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
        alphabetic = true
)
public class Booking {
    @JsonProperty
    private final Long bookingId;

    @JsonProperty
    private final String firstName;

    @JsonProperty
    private final String lastName;

    @JsonProperty
    private final String email;

    @JsonProperty
    private final LocalDate fromDate;

    @JsonProperty
    private final LocalDate toDate;

    @JsonProperty
    private final BookingStatus bookingStatus;

    @JsonProperty
    private String cancellationReason;

    private List<String> days;

    public enum BookingStatus {
        CANCELLED, CONFIRMED
    }

}
