package com.manikanda.moviebookingservice.repo.readRepo;

import com.manikanda.moviebookingservice.entity.read_model.BookingReadModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingReadRepo extends MongoRepository<BookingReadModel, String> {

    Optional<BookingReadModel> findByBookingId(String bookingId);

}
