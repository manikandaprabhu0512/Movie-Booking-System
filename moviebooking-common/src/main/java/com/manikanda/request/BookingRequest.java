package com.manikanda.request;

import java.util.List;

public record BookingRequest(
        String bookingId, String theatreId, String userId, String showId, String screenId, List<String> seatNumbers, String Status, String idempotencyKey
) {
}