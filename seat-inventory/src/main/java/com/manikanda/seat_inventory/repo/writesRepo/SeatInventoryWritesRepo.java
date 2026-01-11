package com.manikanda.seat_inventory.repo.writesRepo;

import com.manikanda.seat_inventory.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SeatInventoryWritesRepo extends JpaRepository<SeatInventory, Long> {

    List<SeatInventory> findByTheatreIdAndShowIdAndScreenIdAndSeatNumberIn(String theatreId, String showId, String screenId, List<String> seatNumbers);

    List<SeatInventory> findByCurrentBookingId(String currentBookingId);

    List<SeatInventory> findByStatusAndReservedUptoBefore(SeatInventory.SeatStatus status, LocalDateTime reservedUptoBefore);
}
