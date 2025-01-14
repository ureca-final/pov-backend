package net.pointofviews.premiere.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "응모 취소 요청 DTO")
public record DeleteEntryRequest(

        @Schema(description = "주문 ID", example = "a4CWyWY5m89PNh7xJwhk1")
        @NotBlank
        String orderId
) {
}
