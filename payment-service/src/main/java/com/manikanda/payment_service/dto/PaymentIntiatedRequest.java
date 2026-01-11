package com.manikanda.payment_service.dto;

import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentIntiatedRequest {

    private String bookingId;
    private String idempotencyKey;
    private BigDecimal price;

}
