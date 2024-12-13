package net.pointofviews.payment.dto.response;

import java.time.LocalDateTime;

public record ConfirmPaymentResponse(
        String paymentKey,
        String orderId,
        int totalAmount,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt
) {
}
