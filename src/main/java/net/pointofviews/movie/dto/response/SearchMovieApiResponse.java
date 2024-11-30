package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "영화 검색 결과 개별 응답")
public record SearchMovieApiResponse(
        @Schema(description = "성인 영화 여부", example = "false")
        boolean adult,

        @Schema(description = "영화의 백드롭 이미지 경로", example = "/8ZTVqvKDQ8emSGUEMjsS4yHAwrp.jpg")
        String backdrop_path,

        @Schema(description = "장르 ID 리스트", example = "[28, 878, 12]")
        List<Integer> genre_ids,

        @Schema(description = "영화 ID", example = "27205")
        int id,

        @Schema(description = "원어 언어", example = "en")
        String original_language,

        @Schema(description = "원제목", example = "Inception")
        String original_title,

        @Schema(description = "영화 개요", example = "타인의 꿈에 들어가 생각을 훔치는 특수 보안요원 코브...")
        String overview,

        @Schema(description = "영화 포스터 이미지 경로", example = "/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg")
        String poster_path,

        @Schema(description = "영화 개봉일", example = "2010-07-15")
        String release_date,

        @Schema(description = "영화 제목", example = "Inception")
        String title
) {
}
