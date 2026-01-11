package com.manikanda.events;

import java.util.List;

public record SeatReservedEvent(
        String bookingId, String theatreId, String showId, String screenId, List<String> seatIds, String status
) {
}