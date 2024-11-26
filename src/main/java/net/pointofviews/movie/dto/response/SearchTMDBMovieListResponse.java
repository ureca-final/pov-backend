package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "TMDB 검색 영화 목록 응답 DTO")
public record SearchTMDBMovieListResponse(
        @Schema(description = "TMDB 영화 검색 결과 목록")
        List<SearchTMDBMovieResponse> movies
) {
}