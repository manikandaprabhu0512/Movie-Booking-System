package com.manikanda.moviebookingservice.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "bookings",
    indexes = {
        @Index(name = "idx_booking_show", columnList = "showId"),
        @Index(name = "idx_booking_status", columnList = "status"),
        @Index(name = "idx_booking_idempotency", columnList = "idempotencyKey")
    }
)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    private String bookingId;

    @Column(nullable = false)
    private String theatreId;

    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String showId;

    @Column(nullable = false)
    private String screenId;

    @Column(nullable = false)
    @ElementCollection
    private List<String> seatNumbers;
    //private Map<String, String> seats; For seatType based pricing

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(unique = true)
    private String idempotencyKey;

    @Column(nullable = false)
    private BigDecimal price;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime paymentExpiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
        this.paymentExpiresAt = createdAt.plusMinutes(10);
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public enum BookingStatus {
        BOOKED,
        PENDING,
        CANCELLED,
        FAILED
    }
}
