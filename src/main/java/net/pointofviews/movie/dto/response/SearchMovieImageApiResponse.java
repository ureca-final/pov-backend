package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "영화에 대한 포스터 이미지 응답입니다.")
public record SearchMovieImageApiResponse(
        @Schema(description = "포스터 이미지 목록")
        List<Poster> posters
) {
    @Schema(description = "포스터 이미지의 상세 정보입니다.")
    public record Poster(
            @Schema(description = "포스터 이미지의 파일 경로", example = "/path/to/poster.jpg", required = true)
            String file_path,

            @Schema(description = "포스터 이미지에 대한 평균 평점", example = "5.5")
            double vote_average,

            @Schema(description = "포스터 이미지의 가로 픽셀 크기", example = "500")
            int width,

            @Schema(description = "포스터 이미지의 세로 픽셀 크기", example = "750")
            int height,

            @Schema(description = "포스터 이미지의 가로세로 비율", example = "0.666")
            double aspect_ratio,

            @Schema(description = "포스터 이미지의 언어 코드 (ISO 639-1)", example = "ko")
            String iso_639_1
    ) {
    }
}
