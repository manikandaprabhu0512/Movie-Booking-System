package com.manikanda.payment_service.entity.read_model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.manikanda.payment_service.entity.Payment.PaymentStatus;

import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "payment_read_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CompoundIndexes({
    @CompoundIndex(name = "idx_payment_booking", def = "{'bookingId': 1}"),
    @CompoundIndex(name = "idx_payment_status", def = "{'status': 1}"),
})
public class PaymentReadModel {

    @Id
    private String paymentId;

    private String bookingId;

    private BigDecimal amount;

    private PaymentStatus status;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
