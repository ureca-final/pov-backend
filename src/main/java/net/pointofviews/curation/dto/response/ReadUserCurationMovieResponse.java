package net.pointofviews.curation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "큐레이션 영화 정보 응답 DTO")
public record ReadUserCurationMovieResponse(
        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "포스터 URL", example = "https://example.com/poster.jpg")
        String poster,

        @Schema(description = "출시일", example = "2010-07-16", format = "date")
        LocalDate released,

        @Schema(description = "영화 좋아요 수", example = "156")
        Long movieLikeCount,

        @Schema(description = "영화 리뷰 수", example = "15")
        Long movieReviewCount
) {
        public ReadUserCurationMovieResponse(String title, String poster, LocalDate released, Long movieLikeCount, Long movieReviewCount) {
                this.title = title;
                this.poster = poster;
                this.released = released;
                this.movieLikeCount = movieLikeCount != null ? movieLikeCount : 0; // Null 처리
                this.movieReviewCount = movieReviewCount != null ? movieReviewCount : 0; // Null 처리
        }
}
