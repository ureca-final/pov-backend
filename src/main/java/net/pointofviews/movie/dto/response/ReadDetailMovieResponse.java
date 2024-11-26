package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "영화 상세 조회 응답 DTO")
public record ReadDetailMovieResponse(
        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "영화 장르 목록", example = "[\"Action\", \"Drama\"]")
        List<String> genre,

        @Schema(description = "감독", example = "Christopher Nolan")
        String director,

        @Schema(description = "작가", example = "Jonathan Nolan")
        String writer,

        @Schema(description = "출연 배우 목록", example = "[\"Leonardo DiCaprio\", \"Joseph Gordon-Levitt\"]")
        List<String> actors,

        @Schema(description = "포스터 URL", example = "https://example.com/poster.jpg")
        String poster,

        @Schema(description = "제작 국가", example = "USA")
        String country,

        @Schema(description = "줄거리", example = "A skilled thief is given a chance to erase his criminal record by completing an impossible heist.")
        String plot,

        @Schema(description = "출시일", example = "2010-07-16", format = "date")
        String released
) {
}