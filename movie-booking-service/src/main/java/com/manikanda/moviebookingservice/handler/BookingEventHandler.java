package com.manikanda.moviebookingservice.handler;

import com.manikanda.events.SeatReservedEvent;
import com.manikanda.moviebookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventHandler {

    private final BookingService bookingService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SeatReservedEvent seatReservedEvent) {

        bookingService.updateSeats(seatReservedEvent);

    }

}
