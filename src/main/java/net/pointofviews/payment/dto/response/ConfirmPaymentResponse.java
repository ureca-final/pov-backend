package net.pointofviews.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ConfirmPaymentResponse(
        String paymentKey,
        String orderId,
        int totalAmount,
        String status,
        LocalDateTime requestedAt,
        LocalDateTime approvedAt
) {
}
