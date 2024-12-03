package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "영화 상세 조회 API 응답 데이터")
public record SearchMovieDetailApiResponse(
        @Schema(description = "성인용 영화 여부", example = "false")
        boolean adult,

        @Schema(description = "영화 배경 이미지 경로", example = "/8ZTVqvKDQ8emSGUEMjsS4yHAwrp.jpg")
        String backdrop_path,

        @ArraySchema(arraySchema = @Schema(description = "영화와 관련된 장르 목록"), minItems = 1)
        List<TMDbGenreResponse> genres,

        @Schema(description = "영화의 고유 ID", example = "27205")
        int id,

        @Schema(description = "영화의 원어", example = "en")
        String original_language,

        @Schema(description = "영화의 원제목", example = "Inception")
        String original_title,

        @Schema(description = "영화에 대한 간단한 설명", example = "타인의 꿈에 들어가 생각을 훔치는 특수 보안요원 코브...")
        String overview,

        @Schema(description = "영화 포스터 이미지 경로", example = "/zTgjeblxSLSvomt6F6UYtpiD4n7.jpg")
        String poster_path,

        @Schema(description = "영화 개봉일", type = "string", format = "date", example = "2010-07-15")
        LocalDate release_date,

        @Schema(description = "영화 상영 시간(분 단위)", example = "148")
        Integer runtime,

        @Schema(description = "영화의 현재 상태", example = "Released")
        String status,

        @Schema(description = "영화 제목", example = "인셉션")
        String title
) {
    @Schema(description = "장르 정보")
    private record TMDbGenreResponse(
            @Schema(description = "장르의 고유 ID", example = "28")
            Integer id,

            @Schema(description = "장르 이름", example = "액션")
            String name
    ) {
    }
}
