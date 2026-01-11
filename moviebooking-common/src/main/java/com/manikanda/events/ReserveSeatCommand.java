package com.manikanda.events;

import java.util.List;

public record ReserveSeatCommand(
        String bookingId, String userId, String theatreId, String showId, String screenId, List<String> seatNumbers
) {
}