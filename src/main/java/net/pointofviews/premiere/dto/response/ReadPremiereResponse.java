package net.pointofviews.premiere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "시사회 정보 응답 DTO")
public record ReadPremiereResponse(
        @Schema(description = "시사회 ID", example = "123")
        Long premiereId,

        @Schema(description = "시사회 제목", example = "Avengers: Endgame World Premiere")
        String title,

        @Schema(description = "시사회 설명", example = "Avengers: Endgame의 월드 프리미어 행사입니다.")
        String description,

        @Schema(description = "시사회 시작 시간", example = "2024-12-15T18:00:00", type = "string")
        LocalDateTime startAt
) {
}
