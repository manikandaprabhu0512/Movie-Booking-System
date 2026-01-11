package com.manikanda.payment_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private String bookingId;
    private BigDecimal amount;
    private LocalDateTime createdAt;

}
