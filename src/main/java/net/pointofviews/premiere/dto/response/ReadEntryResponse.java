package net.pointofviews.premiere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "응모 조회 DTO")
public record ReadEntryResponse(

        @Schema(description = "시사회 ID", example = "1")
        Long premiereId,

        @Schema(description = "시사회 제목", example = "Avengers: Endgame World Premiere")
        String title,

        @Schema(description = "결제 승인 일시", example = "2024-12-15T18:00:00", format = "date-time")
        LocalDateTime approvedAt,

        @Schema(description = "결제 금액", example = "50000")
        int amount
) {
}