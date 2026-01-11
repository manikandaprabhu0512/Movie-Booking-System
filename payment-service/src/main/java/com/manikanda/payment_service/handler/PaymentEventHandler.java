package com.manikanda.payment_service.handler;

import com.manikanda.events.BookingPaymentEvent;
import com.manikanda.payment_service.dto.PaymentIntiatedRequest;
import com.manikanda.payment_service.entity.Payment;
import com.manikanda.payment_service.repo.writes.PaymentWriteRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

import static com.manikanda.common.KafkaConfigProperties.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventHandler {

    private final PaymentWriteRepo paymentWriteRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentIntiatedRequest request) {
        Payment payment = paymentWriteRepo.findByBookingId(request.getBookingId()).orElseGet(() -> createPayment(request));

        boolean paymentStatus = simulatePayment();
        log.info("Payment Service:: Payment Done...");

        if(paymentStatus) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
            BookingPaymentEvent bookingPaymentEvent = new BookingPaymentEvent(
                    payment.getBookingId(),
                    Payment.PaymentStatus.COMPLETED.name()
            );

            kafkaTemplate.send(PAYMENT_EVENT_TOPIC, bookingPaymentEvent.bookingId(), bookingPaymentEvent);
            log.info("Payment Service:: Payment Successful. Success Event has been sent to bookingId: {}", bookingPaymentEvent.bookingId());

        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            BookingPaymentEvent bookingPaymentEvent = new BookingPaymentEvent(
                    payment.getBookingId(),
                    Payment.PaymentStatus.FAILED.name()
            );

            kafkaTemplate.send(PAYMENT_EVENT_TOPIC, bookingPaymentEvent.bookingId(), bookingPaymentEvent);
            log.info("Payment Service:: Payment Failed. Failed Event has been sent to bookingId: {}", bookingPaymentEvent.bookingId());
        }

        paymentWriteRepo.save(payment);
        log.info("Payment Service::Payment Status Updated..");
    }

    private boolean simulatePayment() {
        return Math.random() > 0.3;
    }

    private Payment createPayment(PaymentIntiatedRequest request) {
        Payment payment = new Payment();
        payment.setBookingId(request.getBookingId());
        payment.setIdempotencyKey(request.getIdempotencyKey());
        payment.setAmount(request.getPrice());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentWriteRepo.save(payment);
    }

}
