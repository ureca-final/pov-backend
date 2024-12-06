package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Schema(description = "영화 검색 응답 DTO")
public record SearchMovieResponse(
        @Schema(description = "영화 식별자", example = "1")
        Long id,

        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "포스터 URL", example = "https://example.com/poster.jpg")
        String poster,

        @Schema(description = "출시일", example = "2010-07-16", format = "date")
        Date released,

        @Schema(description = "영화 좋아요 수", example = "600")
        int movieLikeCount,

        @Schema(description = "리뷰 수", example = "98")
        int reviewCount


) {
}