package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "영화 트렌딩 API 응답")
public record SearchMovieTrendingApiResponse(
        @Schema(description = "현재 페이지 번호", example = "1")
        Integer page,

        @Schema(description = "영화 트렌딩 결과 리스트")
        List<TrendingApiResponse> results
) {
    @Schema(description = "트렌딩 API 응답 상세 정보")
    public record TrendingApiResponse(
            @Schema(description = "성인용 콘텐츠 여부", example = "false")
            boolean adult,

            @Schema(description = "백드롭 이미지 경로", example = "/path/to/image.jpg")
            String backdrop_path,

            @Schema(description = "영화 ID", example = "12345")
            int id,

            @Schema(description = "영화 제목", example = "Movie Title")
            String title,

            @Schema(description = "영화 원래 언어", example = "en")
            String original_language,

            @Schema(description = "영화 원래 제목", example = "Original Movie Title")
            String original_title,

            @Schema(description = "영화 개요", example = "This is a movie overview.")
            String overview,

            @Schema(description = "포스터 이미지 경로", example = "/path/to/poster.jpg")
            String poster_path,

            @Schema(description = "미디어 타입", example = "movie")
            String media_type,

            @Schema(description = "장르 ID 목록", example = "[28, 12]")
            List<Integer> genre_ids,

            @Schema(description = "인기도", example = "123.45")
            double popularity,

            @Schema(description = "개봉일", example = "2024-01-01")
            String release_date,

            @Schema(description = "비디오 여부", example = "false")
            boolean video,

            @Schema(description = "평점 평균", example = "7.5")
            double vote_average,

            @Schema(description = "투표 수", example = "1000")
            int vote_count
    ) {
    }
}
