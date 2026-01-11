package com.manikanda.seat_inventory.kafka.producer;

import com.manikanda.events.SeatReservedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.manikanda.common.KafkaConfigProperties.SEAT_RESERVED_TOPIC;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatInventoryProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishSeatReservedEvent(SeatReservedEvent seatReservedEvent) {

        try{
            log.info("SeatReserveProducer:: Publishing seatReserved event for bookingId {}", seatReservedEvent.bookingId());
            kafkaTemplate.send(SEAT_RESERVED_TOPIC, seatReservedEvent.bookingId(), seatReservedEvent);
        } catch (Exception e) {
            log.error("SeatReserveProducer:: Error while publishing seatReserved event for bookingId {}: {}", seatReservedEvent.bookingId(), e.getMessage());
        }

    }
}
