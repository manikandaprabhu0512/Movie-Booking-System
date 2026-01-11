package com.manikanda.payment_service.service.impl;

import com.manikanda.payment_service.dto.PaymentIntiatedRequest;
import com.manikanda.payment_service.dto.PaymentRequest;
import com.manikanda.payment_service.dto.PaymentResponse;
import com.manikanda.payment_service.entity.Payment;
import com.manikanda.payment_service.mapper.PaymentMapper;
import com.manikanda.payment_service.repo.writes.PaymentWriteRepo;
import com.manikanda.payment_service.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentWriteRepo writeRepo;
    private final PaymentMapper paymentMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public PaymentResponse doPayment(PaymentRequest request) {

        log.info("Payment Service:: Amount {}, BookingId {}, IdempotencyKey {}", request.getAmount(), request.getBookingId(), request.getIdempotencyKey());
        Payment payment = paymentMapper.toEntity(request);
        log.info("Payment Service:: Payment Request to Payment...");
        log.info("Payment Service:: Amount {}, BookingId {}, IdempotencyKey {}", payment.getAmount(), payment.getBookingId(), payment.getIdempotencyKey());
        writeRepo.save(payment);
        log.info("Payment Service:: Payment saved for booking id {}", request.getBookingId());

       applicationEventPublisher.publishEvent(
               new PaymentIntiatedRequest(request.getBookingId(), request.getIdempotencyKey(), request.getAmount())
       );

       return paymentMapper.toResponse(payment);
    }
}
