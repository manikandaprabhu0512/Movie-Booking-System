package com.manikanda.payment_service.mapper;

import com.manikanda.payment_service.dto.PaymentRequest;
import com.manikanda.payment_service.dto.PaymentResponse;
import com.manikanda.payment_service.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment toEntity(PaymentRequest request) {
        return Payment.builder()
                .bookingId(request.getBookingId())
                .idempotencyKey(request.getIdempotencyKey())
                .amount(request.getAmount())
                .build();
    }

}
