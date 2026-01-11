package com.manikanda.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BookingResponse(
        String bookingId, String theatreId, String showId, String screenId, List<String> seatNumbers, String userId, String Status, BigDecimal price, LocalDateTime createdAt
) {
}