package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

@Schema(description = "영화 응답 리스트 DTO")
public record MovieListResponse(
        @Schema(description = "영화 리스트")
        Slice<MovieResponse> movies
) {
}