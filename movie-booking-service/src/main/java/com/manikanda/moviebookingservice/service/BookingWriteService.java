package com.manikanda.moviebookingservice.service;

import com.manikanda.events.SeatReservedEvent;
import com.manikanda.request.BookingRequest;
import com.manikanda.response.BookingResponse;

public interface BookingWriteService {

    BookingResponse createBooking(BookingRequest request);

    void updateSeats(SeatReservedEvent seatReservedEvent);
}
