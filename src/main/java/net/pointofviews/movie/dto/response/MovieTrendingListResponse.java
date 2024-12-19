package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "영화 응답 리스트 DTO")
public record MovieTrendingListResponse(
        @Schema(description = "영화 트렌딩 응답 리스트")
        List<MovieTrendingResponse> movies
) {
}