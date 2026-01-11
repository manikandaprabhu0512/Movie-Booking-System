package com.manikanda.seat_inventory.kafka.consumer;

import com.manikanda.events.BookingPaymentEvent;
import com.manikanda.seat_inventory.service.SeatInventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.manikanda.events.ReserveSeatCommand;

import static com.manikanda.common.KafkaConfigProperties.*;
import static com.manikanda.common.KafkaConfigProperties.MOVIE_BOOKING_GROUP;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatInventoryListener {

    private final SeatInventoryService inventoryService;

    @KafkaListener(topics = SEAT_RESERVED_CMD_TOPIC, groupId = SEAT_EVENT_GROUP)
    public void reserveSeatCommand(ReserveSeatCommand reserveSeatCommand) {
        log.info("Seat Inventory:: Consuming seat reserve event...");

        inventoryService.reserveSeat(reserveSeatCommand);
    }

    @KafkaListener(topics = PAYMENT_EVENT_TOPIC, groupId = MOVIE_BOOKING_GROUP)
    @Transactional
    public void paymentEvent(BookingPaymentEvent bookingPaymentEvent) {
        log.info("Seat Inventory:: Consuming Payment completed event...");

        inventoryService.bookSeat(bookingPaymentEvent);
    }



}
