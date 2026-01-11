package com.manikanda.events;

import java.time.LocalDateTime;
import java.util.List;

public record BookingCreatedEvent(
        String bookingId, String theatreId, String showId, String screenId, String userId, List<String> seatIds, String status, LocalDateTime createdAt
) {
}