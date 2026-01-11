package com.manikanda.moviebookingservice.service.BookingServiceImpl;

import com.manikanda.events.BookingCreatedEvent;
import com.manikanda.events.ReserveSeatCommand;
import com.manikanda.events.SeatReservedEvent;
import com.manikanda.moviebookingservice.entity.Booking;
import com.manikanda.moviebookingservice.entity.read_model.BookingReadModel;
import com.manikanda.moviebookingservice.mapper.BookingMapper;
import com.manikanda.moviebookingservice.repo.readRepo.BookingReadRepo;
import com.manikanda.moviebookingservice.repo.writeRepo.BookingWriteRepo;
import com.manikanda.moviebookingservice.service.BookingService;
import com.manikanda.request.BookingRequest;
import com.manikanda.response.BookingResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.manikanda.common.KafkaConfigProperties.*;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingWriteRepo bookingWriteRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final BookingMapper bookingMapper;
    private final BookingReadRepo bookingReadRepo;

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        System.out.println("Requesting for booking...");

//        If Booking Orchestrator is a different Service then Event will be sent by the Orchestrator to create booking.
//        CreateBookingEvent bookingEvent = new CreateBookingEvent(
//            request.bookingId(), request.theatreId(), request.userId(), request.showId(),request.seatIds(), request.price(), request.idempotencyKey(), "PENDING"
//        );
//
//        kafkaTemplate.send(MOVIE_BOOKING_EVENTS_TOPIC, request.bookingId(), bookingEvent);

        Booking booking = bookingMapper.toEntity(request);
        System.out.println("Request Converted to Booking for id: " + booking.getBookingId() + " booking");

        Booking savedBooking = bookingWriteRepo.save(booking);
        System.out.println("Booking saved...");

        //Possible Error Cause: Data may fail storing in the DB. Which may cause the inconsistency.
//        //To avoid this follow AFTER-COMMIT or Outboxing.

        ReserveSeatCommand reserverSeat = new ReserveSeatCommand(
            booking.getBookingId(), booking.getUserId(), booking.getTheatreId(), booking.getShowId(), booking.getScreenId(), booking.getSeatNumbers()
        );

        kafkaTemplate.send(SEAT_RESERVED_CMD_TOPIC, request.bookingId(), reserverSeat);
        log.info("MovieBookingService:: Published seatReserve request for bookingId: {}", booking.getBookingId());

        //Else pass booking directly instead of booking created event.
        BookingCreatedEvent bookingCreatedEvent;
        bookingCreatedEvent = new BookingCreatedEvent(
                booking.getBookingId(), booking.getTheatreId(), booking.getShowId(), booking.getScreenId(), booking.getUserId(), booking.getSeatNumbers(), Booking.BookingStatus.PENDING.name(), LocalDateTime.now()
        );

        kafkaTemplate.send(UPDATE_READ_REPO_EVENT_TOPIC, bookingCreatedEvent.bookingId(), bookingCreatedEvent);
        log.info("MovieBookingService:: Published save to ReadDB request for bookingId: {}", request.bookingId());

        return bookingMapper.toResponse(savedBooking);
    }

    @Override
    public void updateSeats(SeatReservedEvent seatReservedEvent) {
        BookingReadModel bookingReadModel =  bookingReadRepo.findByBookingId(seatReservedEvent.bookingId()).orElseGet(() -> createNewModel(seatReservedEvent));

        Booking.BookingStatus newStatus = seatReservedEvent.status().equals("SUCCESS") ? Booking.BookingStatus.PENDING : Booking.BookingStatus.FAILED;

        log.info("Updating read model for booking... ");

        bookingReadModel.setStatus(newStatus.name());

        bookingReadRepo.save(bookingReadModel);
    }

    private BookingReadModel createNewModel(SeatReservedEvent seatReservedEvent) {
        BookingReadModel model = new BookingReadModel();
        model.setBookingId(seatReservedEvent.bookingId());
        model.setTheatreId(seatReservedEvent.theatreId());
        model.setShowId(seatReservedEvent.showId());
        model.setStatus(seatReservedEvent.status());
        model.setCreatedAt(LocalDateTime.now());

        return model;
    }
}
