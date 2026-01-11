package com.manikanda.events;

import java.math.BigDecimal;
import java.util.List;

public record CreateBookingEvent(
        String bookingId, String theatreId, String userId, String showId, List<String> seatIds, BigDecimal price, String idempotencyKey, String status
) {}