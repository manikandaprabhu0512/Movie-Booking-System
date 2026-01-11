package com.manikanda.payment_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Booking Id is required")
    private String bookingId;

    @NotNull(message = "IdempotencyKey is required")
    private String idempotencyKey;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;


}
