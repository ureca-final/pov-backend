package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

@Schema(description = "영화 검색 결과 리스트 응답")
public record SearchMovieApiListResponse(
        @Schema(description = "요청 페이지 번호 (1 ~ 500)", example = "1")
        @Min(value = 1)
        @Max(value = 500)
        int page,

        @Schema(description = "검색 결과의 총 페이지 수", example = "10")
        int total_pages,

        @Schema(description = "검색된 영화의 총 개수", example = "100")
        int total_results,

        @Schema(description = "검색된 영화 리스트")
        List<SearchMovieApiResponse> results
) {
}
