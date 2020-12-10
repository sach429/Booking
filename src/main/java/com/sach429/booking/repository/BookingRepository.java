package com.sach429.booking.repository;

import com.sach429.booking.model.Booking;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends CrudRepository<Booking, ObjectId> {

    @Query(value = "{'bookingId':?0}")
    Booking getBookingByBookingId(Long id);

}
