package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "영화 응답 DTO")
public record MovieResponse(
        @Schema(description = "영화 식별자", example = "1")
        Long id,

        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "포스터 URL", example = "https://example.com/poster.jpg")
        String poster,

        @Schema(description = "출시일", example = "2010-07-16", format = "date")
        LocalDate released,

        @Schema(description = "좋아요 여부", example = "true")
        boolean isLiked,

        @Schema(description = "영화 좋아요 수", example = "156")
        Long movieLikeCount,

        @Schema(description = "영화 리뷰 수", example = "15")
        Long movieReviewCount
) {
}