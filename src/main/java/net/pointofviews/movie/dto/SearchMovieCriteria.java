package net.pointofviews.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화 검색 조건")
public record SearchMovieCriteria(
        @Schema(description = "정렬 기준 (recently 또는 popularity)", example = "recently")
        String sortBy,

        @Schema(description = "영화 장르", example = "Action")
        String genre,

        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "출연 배우", example = "Leonardo DiCaprio")
        String actor,

        @Schema(description = "작가", example = "Jonathan Nolan")
        String writer,

        @Schema(description = "감독", example = "Christopher Nolan")
        String director
) {
}
