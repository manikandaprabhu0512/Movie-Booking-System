package com.manikanda.seat_inventory.scheduler;

import com.manikanda.seat_inventory.entity.SeatInventory;
import com.manikanda.seat_inventory.repo.writesRepo.SeatInventoryWritesRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeatInventoryScheduler {

    private final SeatInventoryWritesRepo writesRepo;

    @Scheduled(fixedDelayString = "60000")
    @Transactional
    public void run() {
        LocalDateTime now = LocalDateTime.now();

        List<SeatInventory> expiredReservedSeats = writesRepo.findByStatusAndReservedUptoBefore(SeatInventory.SeatStatus.RESERVED, now);

        for(SeatInventory seat : expiredReservedSeats) {
            seat.setStatus(SeatInventory.SeatStatus.AVAILABLE);

            writesRepo.save(seat);

            //TODO: Update Read Model Seat Status to AVAILABLE.

            log.info(
                    "Seat {} released due to payment timeout",
                    seat.getSeatNumber()
            );
        }

    }

}
