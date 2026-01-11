package com.manikanda.moviebookingservice.scheduler;

import com.manikanda.moviebookingservice.entity.Booking;
import com.manikanda.moviebookingservice.repo.writeRepo.BookingWriteRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingTimeoutScheduler {

    private final BookingWriteRepo writeRepo;

    @Scheduled(fixedDelayString = "60000")
    @Transactional
    public void run() {

        LocalDateTime now = LocalDateTime.now();

        List<Booking> expiredBookings = writeRepo.findByStatusAndPaymentExpiresAtBefore(Booking.BookingStatus.PENDING, now);

        if (expiredBookings.isEmpty()) {
            return;
        }

        for (Booking booking : expiredBookings) {

            booking.setStatus(Booking.BookingStatus.FAILED);
            writeRepo.save(booking);

            //TODO: Update Read Model Booking Status to FAILED.

            log.info(
                    "Booking {} cancelled due to payment timeout",
                    booking.getBookingId()
            );
        }

    }
}
