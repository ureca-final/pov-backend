package net.pointofviews.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 임시 저장 DTO")
public record TempPaymentDto(

        @Schema(description = "주문 ID", example = "a4CWyWY5m89PNh7xJwhk1")
        String orderId,

        @Schema(description = "총 금액", example = "50000")
        int amount
) {
}
