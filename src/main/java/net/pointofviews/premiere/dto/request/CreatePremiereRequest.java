package net.pointofviews.premiere.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreatePremiereRequest(
        @Schema(description = "시사회 내용", example = "Avengers: Endgame의 월드 프리미어 행사입니다.")
        String content,

        @Schema(description = "시사회 제목", example = "Avengers: Endgame World Premiere")
        String title,

        @Schema(description = "이벤트 이미지 URL", example = "https://example.com/images/premiere.jpg")
        String image,

        @Schema(description = "시사회 시작 날짜", example = "2024-12-15", format = "date")
        String startAt,

        @Schema(description = "결제 필요 여부", example = "true")
        Boolean isPaymentRequired
) {
}