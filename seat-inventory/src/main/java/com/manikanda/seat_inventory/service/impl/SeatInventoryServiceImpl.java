package com.manikanda.seat_inventory.service.impl;

import com.manikanda.events.BookingPaymentEvent;
import com.manikanda.events.ReserveSeatCommand;
import com.manikanda.events.SeatReservedEvent;
import com.manikanda.seat_inventory.entity.SeatInventory;
import com.manikanda.seat_inventory.entity.read_model.SeatInventoryReadModel;
import com.manikanda.seat_inventory.kafka.producer.SeatInventoryProducer;
import com.manikanda.seat_inventory.repo.readsRepo.SeatInventoryReadsRepo;
import com.manikanda.seat_inventory.repo.writesRepo.SeatInventoryWritesRepo;
import com.manikanda.seat_inventory.service.SeatInventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SeatInventoryServiceImpl implements SeatInventoryService {

    private final SeatInventoryWritesRepo writesRepo;
    private final SeatInventoryProducer seatInventoryProducer;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final SeatInventoryReadsRepo seatInventoryReadsRepo;

    @Override
    @Transactional
    public void reserveSeat(ReserveSeatCommand reserveSeatCommand) {

        List<SeatInventory> seats = writesRepo.findByTheatreIdAndShowIdAndScreenIdAndSeatNumberIn(reserveSeatCommand.theatreId(), reserveSeatCommand.showId(), reserveSeatCommand.screenId(), reserveSeatCommand.seatNumbers());

        boolean allAvailable = seats.stream()
                .allMatch(s -> s.getStatus() == SeatInventory.SeatStatus.AVAILABLE);

        //TODO: Check weather seats are already booked;

        log.info("Seats can be reserved: {}", allAvailable);

        LocalDateTime now = LocalDateTime.now();

        if(allAvailable) {
            seats.forEach(s -> {
                s.setStatus(SeatInventory.SeatStatus.RESERVED);
                s.setCurrentBookingId(reserveSeatCommand.bookingId());
                s.setReservedUpto(now.plusMinutes(10));
            });

            writesRepo.saveAll(seats);
            log.info("Saved to write DB...");

            applicationEventPublisher.publishEvent(
                    new SeatReservedEvent(
                            reserveSeatCommand.bookingId(),
                            reserveSeatCommand.theatreId(),
                            reserveSeatCommand.showId(),
                            reserveSeatCommand.screenId(),
                            reserveSeatCommand.seatNumbers(),
                            SeatReservationStatus.SUCCESS.name()
                    )
            );

        } else {
            log.warn("SeatInventoryService:: Seat locking failed for bookingId {}. Some seats are not available.", reserveSeatCommand.bookingId());
            SeatReservedEvent seatReservedEvent = new SeatReservedEvent(
                    reserveSeatCommand.bookingId(), reserveSeatCommand.theatreId(), reserveSeatCommand.showId(), reserveSeatCommand.screenId(), reserveSeatCommand.seatNumbers(), SeatReservationStatus.FAILED_NOT_AVAILABLE.name()
            );
            seatInventoryProducer.publishSeatReservedEvent(seatReservedEvent);
        }

    }

    @Override
    public void updateSeats(SeatReservedEvent seatReservedEvent) {
        log.info("Updating Read Model...");

        SeatInventoryReadModel seatInventoryReadModel = seatInventoryReadsRepo.findByTheatreIdAndShowIdAndScreenId(seatReservedEvent.theatreId(), seatReservedEvent.showId(), seatReservedEvent.screenId()).orElseGet(() -> createNewModel(seatReservedEvent));

        SeatReservationStatus reservationStatus = SeatReservationStatus.valueOf(seatReservedEvent.status());

        SeatInventoryReadModel.SeatStatus newStatus = reservationStatus == SeatReservationStatus.SUCCESS ? SeatInventoryReadModel.SeatStatus.RESERVED : SeatInventoryReadModel.SeatStatus.AVAILABLE;

        for (String seat : seatReservedEvent.seatIds()) {
            seatInventoryReadModel.getSeats().put(seat, newStatus);
        }

        seatInventoryReadModel.setUpdatedAt(LocalDateTime.now());

        seatInventoryReadsRepo.save(seatInventoryReadModel);
        log.info("Seat Inventory Read Model Saved...");
    }

    @Override
    public void bookSeat(BookingPaymentEvent bookingPaymentEvent) {
        List<SeatInventory> seats = writesRepo.findByCurrentBookingId(bookingPaymentEvent.bookingId());

        if (seats.isEmpty()) {
            log.warn(
                    "No seats found for bookingId {}. Ignoring payment event.",
                    bookingPaymentEvent.bookingId()
            );
            return;
        }

//        if(bookingPaymentEvent.paymentStatus().equals("COMPLETED")) {
//            for(SeatInventory seat : seats) {
//                seat.setStatus(SeatInventory.SeatStatus.BOOKED);
//            }
//        } else {
//            for(SeatInventory seat : seats) {
//                seat.setStatus(SeatInventory.SeatStatus.RESERVED);
//            }
//        }

        for(SeatInventory seat : seats) {
            if(bookingPaymentEvent.paymentStatus().equals("COMPLETED")) {
                seat.setStatus(SeatInventory.SeatStatus.BOOKED);
            } else {
                seat.setStatus(SeatInventory.SeatStatus.RESERVED);
            }
        }

        writesRepo.saveAll(seats);
        log.info("Seat Inventory Service:: Update Seat Status on Payment Status for bookingId: {}", bookingPaymentEvent.bookingId());

        //TODO: Send Event to Update it in the Read Model.
        log.info("Booking Service:: Updated to Seat Inventory Read Model...");
    }

    private SeatInventoryReadModel createNewModel(SeatReservedEvent seatReservedEvent) {

        SeatInventoryReadModel model = new SeatInventoryReadModel();
        model.setId(seatReservedEvent.theatreId() + "-" + seatReservedEvent.showId());
        model.setTheatreId(seatReservedEvent.theatreId());
        model.setShowId(seatReservedEvent.showId());
        model.setScreenId(seatReservedEvent.screenId());
        model.setSeats(new HashMap<>());
        model.setUpdatedAt(LocalDateTime.now());

        log.info("Created New Model...");

        return model;
    }

    public enum SeatReservationStatus {
        SUCCESS,
        FAILED_NOT_AVAILABLE,
        FAILED_ALREADY_BOOKED
    }
}
