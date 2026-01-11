package com.manikanda.moviebookingservice.controller;

import com.manikanda.moviebookingservice.entity.Booking;
import com.manikanda.moviebookingservice.mapper.BookingMapper;
import com.manikanda.moviebookingservice.repo.writeRepo.BookingWriteRepo;
import com.manikanda.moviebookingservice.service.BookingService;
import com.manikanda.request.BookingRequest;
import com.manikanda.response.BookingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;
    private final BookingWriteRepo bookingWriteRepo;
    private final BookingMapper bookingMapper;

    @PostMapping("/create-booking")
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingRequest request) {
        try {
            Optional<Booking> existingBooking = bookingWriteRepo.findByIdempotencyKey(request.idempotencyKey());

            if (existingBooking.isPresent()) {
                Booking booking = existingBooking.get();

                BookingResponse response = bookingMapper.toResponse(booking);

                return ResponseEntity.accepted().body(response);
            }

            log.info("Create Booking initiated...");

            BookingResponse response = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
