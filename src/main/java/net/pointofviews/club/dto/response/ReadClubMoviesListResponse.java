package net.pointofviews.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "클럽 북마크 영화 전체 리스트 응답 DTO")
public record ReadClubMoviesListResponse(
        @Schema(description = "클럽 북마크 영화 리스트")
        List<ReadClubMovieResponse> clubMovies
) {}