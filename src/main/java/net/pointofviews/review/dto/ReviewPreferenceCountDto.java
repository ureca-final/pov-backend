package net.pointofviews.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화 리뷰 선호도")
public record ReviewPreferenceCountDto(
        @Schema(description = "선호 갯수", example = "5")
        Long goodCount,

        @Schema(description = "불호 갯수", example = "5")
        Long badCount
) {
}
