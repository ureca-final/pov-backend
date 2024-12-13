package net.pointofviews.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토스 결제 승인 요청 DTO")
public record ConfirmPaymentRequest(

        @Schema(description = "토스 결제키", example = "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1")
        String paymentKey,

        @Schema(description = "주문 ID", example = "a4CWyWY5m89PNh7xJwhk1")
        String orderId,

        @Schema(description = "결제 총 금액", example = "50000")
        int amount
) {
}
