package net.pointofviews.premiere.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "응모 생성 요청 DTO")
public record CreateEntryRequest(

        @Schema(description = "응모 수량", example = "1")
        @NotNull
        int quantity,

        @Schema(description = "총 금액", example = "50000")
        @NotNull
        int amount
) {
}
