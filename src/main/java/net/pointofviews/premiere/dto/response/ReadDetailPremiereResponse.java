package net.pointofviews.premiere.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ReadDetailPremiereResponse(
        @Schema(description = "시사회 ID", example = "123")
        Long premiereId,

        @Schema(description = "시사회 제목", example = "Avengers: Endgame World Premiere")
        String title,

        @Schema(description = "시사회 설명", example = "Avengers: Endgame의 월드 프리미어 행사입니다.")
        String description,

        @Schema(description = "시사회 시작 시간", example = "2024-12-15T18:00:00", type = "string")
        LocalDateTime startAt,

        @Schema(
                description = "시사회 내용 (Web Editor를 통해 작성된 HTML 또는 Markdown 데이터)",
                example = "<p>이 시사회는 Avengers: Endgame의 월드 프리미어로, 주요 배우와 감독이 참석합니다.</p>"
        )
        String content,

        @Schema(
                description = "시사회 이벤트 이미지 URL",
                example = "https://example.com/images/avengers-premiere.jpg"
        )
        String eventImage
) {
}