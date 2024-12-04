package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "TMDB 영화 심의 등급 정보 응답 DTO")
public record SearchReleaseApiResponse(
        @Schema(description = "영화 ID", example = "27205")
        int id,

        @Schema(description = "결과 목록")
        List<Result> results
) {

    @Schema(description = "결과")
    public record Result(
            @Schema(description = "ISO 3166-1 국가 코드", example = "KR")
            String iso_3166_1,

            @Schema(description = "릴리즈 날짜 목록")
            List<ReleaseDate> release_dates
    ) {

        @Schema(description = "릴리즈 날짜 정보")
        public record ReleaseDate(
                @Schema(description = "심의 등급", example = "12")
                String certification,

                @Schema(description = "ISO 639-1 언어 코드", example = "ko")
                String iso_639_1,

                @Schema(description = "릴리즈 날짜", example = "2010-07-21T00:00:00.000Z", format = "date-time")
                String release_date,

                @Schema(description = "릴리즈 타입", example = "3")
                int type,

                @Schema(description = "메모", example = "Some note")
                String note
        ) {
        }
    }
}
