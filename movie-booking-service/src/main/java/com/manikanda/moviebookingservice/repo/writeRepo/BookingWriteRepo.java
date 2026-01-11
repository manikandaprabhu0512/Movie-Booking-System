package com.manikanda.moviebookingservice.repo.writeRepo;

import com.manikanda.moviebookingservice.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingWriteRepo extends JpaRepository<Booking, String> {

    Optional<Booking> findByIdempotencyKey(String bookingCode);

    Optional<Booking> findByBookingId(String bookingId);

    List<Booking> findByStatusAndPaymentExpiresAtBefore(Booking.BookingStatus status, LocalDateTime paymentExpiresAtBefore);

}
