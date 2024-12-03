package net.pointofviews.movie.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "영화 등록 요청 DTO")
public record CreateMovieRequest(
        @Schema(description = "영화 제목", example = "Inception", requiredMode = Schema.RequiredMode.REQUIRED)
        String title,

        @Schema(description = "영화 장르 목록", example = "[\"SF\", \"액션\", \"모험\"]")
        List<String> genre,

        @Schema(description = "영화 감독", example = "Christopher Nolan")
        String director,

        @Schema(description = "영화 작가", example = "Christopher Nolan")
        String writer,

        @Schema(description = "출연 배우 목록", example = "[\"Leonardo DiCaprio\", \"Joseph Gordon-Levitt\"]")
        List<String> actors,

        @Schema(description = "포스터 이미지 URL", example = "https://example.com/poster.jpg")
        String poster,

        @Schema(description = "제작 국가", example = "USA")
        String country,

        @Schema(description = "영화 줄거리", example = "A skilled thief is given a chance at redemption.")
        String plot,

        @Schema(description = "영화 출시일", example = "2010-07-16", type = "string", format = "date")
        String released,

        @Schema(description = "TMDb ID", example = "27205")
        String tmdbId
) {
}