package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "클럽별 영화 북마크 정보 응답 DTO")
public record ReadClubMovieResponse(
        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "포스터 URL", example = "https://example.com/poster.jpg")
        String poster,

        @Schema(description = "출시일", example = "2010-07-16", format = "date")
        int released,

        @Schema(description = "영화 좋아요 수", example = "156")
        int movieLikeCount,

        @Schema(description = "영화 리뷰 수", example = "156")
        int movieReviewCount
) {}
