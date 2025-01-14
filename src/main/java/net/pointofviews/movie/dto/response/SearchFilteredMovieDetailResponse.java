package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "필터링된 영화 상세 정보 응답 DTO")
public record SearchFilteredMovieDetailResponse(
        @Schema(description = "TMDB 영화 ID", example = "27205")
        Integer tmdbId,

        @Schema(description = "영화 제목", example = "Inception")
        String title,

        @Schema(description = "영화 줄거리", example = "Cobb, a skilled thief who commits corporate espionage by infiltrating the subconscious of his targets is offered a chance to regain his old life...")
        String plot,

        @Schema(description = "영화 포스터 경로", example = "/oYuLEt3zVCKq57qu2F8dT7NIa6f.jpg")
        String poster,

        @Schema(description = "영화 배경 이미지 경로", example = "/8ZTVqvKDQ8emSGUEMjsS4yHAwrp.jpg")
        String backdrop,

        @ArraySchema(arraySchema = @Schema(description = "제작 국가 목록", example = "[\"US\", \"GB\"]"))
        List<String> originCountries,

        @Schema(description = "영화 개봉일", example = "2010-07-15")
        String released,

        @Schema(description = "영화 심의 등급", example = "12")
        String filmRating,

        @ArraySchema(arraySchema = @Schema(description = "영화 장르 목록", example = "[\"Action\", \"Science Fiction\", \"Adventure\"]"))
        List<String> genres,

        @Schema(description = "영화 출연진 및 제작진 정보")
        SearchCreditApiResponse peoples
) {

    public static SearchFilteredMovieDetailResponse of(
            SearchMovieDetailApiResponse movieDetails,
            String released,
            String filmRating,
            List<String> genres,
            SearchCreditApiResponse peoples
    ) {
        return new SearchFilteredMovieDetailResponse(
                movieDetails.id(),
                movieDetails.title(),
                movieDetails.overview(),
                movieDetails.poster_path(),
                movieDetails.backdrop_path(),
                movieDetails.origin_country(),
                released,
                filmRating,
                genres,
                peoples
        );
    }
}
