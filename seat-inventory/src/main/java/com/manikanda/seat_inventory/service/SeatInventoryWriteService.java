package com.manikanda.seat_inventory.service;

import com.manikanda.events.BookingPaymentEvent;
import com.manikanda.events.ReserveSeatCommand;
import com.manikanda.events.SeatReservedEvent;

public interface SeatInventoryWriteService {

    void reserveSeat(ReserveSeatCommand reserveSeatCommand);

    void updateSeats(SeatReservedEvent seatReservedEvent);

    void bookSeat(BookingPaymentEvent bookingPaymentEvent);
}
