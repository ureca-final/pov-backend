package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "영화 검색 결과 개별 응답")
public record SearchMovieApiResponse(
        @Schema(description = "성인 영화 여부", example = "false")
        boolean adult,

        @Schema(description = "장르 ID 리스트", example = "[28, 878, 12]")
        List<String> genre_ids,

        @Schema(description = "영화 ID", example = "27205")
        int id,

        @Schema(description = "영화 포스터 이미지 경로", example = "/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg")
        String poster_path,

        @Schema(description = "영화 개봉일", example = "2010-07-15")
        String release_date,

        @Schema(description = "영화 제목", example = "Inception")
        String title
) {
}
