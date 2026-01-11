package com.manikanda.moviebookingservice.kafka.consumer;

import com.manikanda.events.BookingCreatedEvent;
import com.manikanda.events.BookingPaymentEvent;
import com.manikanda.events.SeatReservedEvent;
import com.manikanda.moviebookingservice.entity.Booking;
import com.manikanda.moviebookingservice.entity.read_model.BookingReadModel;
import com.manikanda.moviebookingservice.mapper.BookingMapper;
import com.manikanda.moviebookingservice.repo.readRepo.BookingReadRepo;
import com.manikanda.moviebookingservice.repo.writeRepo.BookingWriteRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.manikanda.common.KafkaConfigProperties.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingEventListener {

    private final BookingReadRepo bookingReadRepo;
    private final BookingWriteRepo bookingWriteRepo;
    private final BookingMapper bookingMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @KafkaListener(topics = UPDATE_READ_REPO_EVENT_TOPIC, groupId = MOVIE_BOOKING_GROUP)
    public void saveWritestoReadsDB(BookingCreatedEvent createdEvent) {
        BookingReadModel bookingReadModel = bookingMapper.toReadModel(createdEvent);

        bookingReadRepo.insert(bookingReadModel);
        log.info("MovieBookingService:: Booking for booking id: {}, saved in Read DB", createdEvent.bookingId());
    }

    @KafkaListener(topics = SEAT_RESERVED_TOPIC, groupId = MOVIE_BOOKING_GROUP)
    @Transactional
    public void seatReservedEvent(SeatReservedEvent event) {

        Booking booking = bookingWriteRepo.findByBookingId(event.bookingId()).orElseThrow(() ->new IllegalArgumentException("Booking not found for bookingId " + event.bookingId()));

        if (event.status().equals("FAILED_NOT_AVAILABLE")) {
            booking.setStatus(Booking.BookingStatus.FAILED);
            log.info("MovieBookingService:: Marked booking failed for bookingId {}", event.bookingId());
            bookingWriteRepo.save(booking);
        }

        applicationEventPublisher.publishEvent(event);
    }

    @KafkaListener(topics = PAYMENT_EVENT_TOPIC, groupId = MOVIE_BOOKING_GROUP)
    @Transactional
    public void paymentEvent(BookingPaymentEvent bookingPaymentEvent) {

        Booking booking = bookingWriteRepo.findByBookingId(bookingPaymentEvent.bookingId()).orElseThrow(() ->new IllegalArgumentException("Booking not found for bookingId " + bookingPaymentEvent.bookingId())); //Handle the error.

        if(bookingPaymentEvent.paymentStatus().equals("COMPLETED")) {
            booking.setStatus(Booking.BookingStatus.BOOKED);
        } else {
            booking.setStatus(Booking.BookingStatus.PENDING);
        }

        bookingWriteRepo.save(booking);
        log.info("Booking Service:: Update Booking Status on Payment Status for bookingId: {}", bookingPaymentEvent.bookingId());

        //TODO: Send Event to Update it in the Read Model.
        log.info("Booking Service:: Updated to Booking Read Model...");
    }

}
