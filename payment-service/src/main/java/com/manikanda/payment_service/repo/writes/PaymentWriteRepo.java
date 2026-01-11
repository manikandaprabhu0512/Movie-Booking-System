package com.manikanda.payment_service.repo.writes;

import com.manikanda.payment_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentWriteRepo extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(String bookingId);
    
}
