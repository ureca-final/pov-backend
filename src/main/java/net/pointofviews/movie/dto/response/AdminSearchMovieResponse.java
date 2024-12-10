package net.pointofviews.movie.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "큐레이션 영화 검색 응답 DTO")
public record AdminSearchMovieResponse(
        @Schema(description = "영화 id", example = "1")
        Long id,

        @Schema(description = "영화 제목", example = "인터스텔라")
        String title,

        @Schema(description = "출시일", example = "2010-07-16", format = "date")
        LocalDate released
) {}