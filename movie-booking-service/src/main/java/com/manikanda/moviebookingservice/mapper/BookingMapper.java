package com.manikanda.moviebookingservice.mapper;

import com.manikanda.events.BookingCreatedEvent;
import com.manikanda.moviebookingservice.entity.Booking;
import com.manikanda.moviebookingservice.entity.read_model.BookingReadModel;
import com.manikanda.request.BookingRequest;
import com.manikanda.response.BookingResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class BookingMapper {

    public BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getBookingId(),
                booking.getTheatreId(),
                booking.getShowId(),
                booking.getScreenId(),
                booking.getSeatNumbers(),
                booking.getUserId(),
                booking.getStatus().name(),
                booking.getPrice(),
                booking.getCreatedAt()
        );
    }

    public Booking toEntity(BookingRequest request) {
        //TODO: Add Pricing Service or Pricing table for SeatType Pricing.
        int pricePerSeat = 250; //hardcoded value
        BigDecimal price = BigDecimal.ZERO;

        price = BigDecimal.valueOf((long) request.seatNumbers().size() * pricePerSeat);

        String bookingId = UUID.randomUUID().toString();

        return Booking.builder()
                .bookingId(bookingId)
                .theatreId(request.theatreId())
                .userId(request.userId())
                .showId(request.showId())
                .screenId(request.screenId())
                .seatNumbers(request.seatNumbers())
                .idempotencyKey(request.idempotencyKey())
                .price(price)
                .build();
    }

    public BookingReadModel toReadModel(BookingCreatedEvent createdEvent) {
        int pricePerSeat = 250; //hardcoded value
        BigDecimal price = BigDecimal.ZERO;

        price = BigDecimal.valueOf((long) createdEvent.seatIds().size() * pricePerSeat);

        return BookingReadModel.builder()
                .bookingId(createdEvent.bookingId())
                .userId(createdEvent.userId())
                .theatreId(createdEvent.theatreId())
                .showId(createdEvent.showId())
                .seatNumbers(createdEvent.seatIds())
                .status(createdEvent.status())
                .price(price)
                .createdAt(createdEvent.createdAt())
                .build();
    }

}
