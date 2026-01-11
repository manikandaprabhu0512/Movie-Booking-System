package com.manikanda.events;


public record BookingPaymentEvent(
        String bookingId, String paymentStatus
) {
}