package com.manikanda.seat_inventory.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "seat_inventory",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_show_screen_seat",
            columnNames = {"showId", "screenId", "seatNumber"}
        )
    },
    indexes = {
        @Index(name = "idx_seat_lookup", columnList = "theatreId,showId,seatNumber"),
        @Index(name = "idx_seat_booking", columnList = "currentBookingId")
    }
)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String theatreId;

    @Column(nullable = false)
    private String showId;

    @Column(nullable = false)
    private String screenId;

    @Column(nullable = false)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SeatStatus status = SeatStatus.AVAILABLE;

    private String currentBookingId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime reservedUpto;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum SeatStatus {
        BOOKED,
        RESERVED,
        AVAILABLE
    }

}
