package com.manikanda.seat_inventory.handler;

import com.manikanda.events.SeatReservedEvent;
import com.manikanda.seat_inventory.kafka.producer.SeatInventoryProducer;
import com.manikanda.seat_inventory.service.SeatInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatInventoryEventHandler {

    private final SeatInventoryProducer seatInventoryProducer;
    private final SeatInventoryService seatInventoryService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(SeatReservedEvent seatReservedEvent) {

        seatInventoryProducer.publishSeatReservedEvent(seatReservedEvent);

        log.info("SeatInventory:: Published Seat Reserved Event to Orchestrator...");

        seatInventoryService.updateSeats(seatReservedEvent);

    }

}
