package net.pointofviews.premiere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "응모 성공 응답 DTO")
public record CreateEntryResponse(

        @Schema(description = "주문 ID")
        String orderId
) {
}
