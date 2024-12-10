package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Slice;

@Schema(description = "큐레이션 영화 검색 응답 리스트 DTO")
public record AdminSearchMovieListResponse(
        @Schema(description = "큐레이션 영화 검색 응답 리스트")
        Slice<AdminSearchMovieResponse> curationMovies
) {}