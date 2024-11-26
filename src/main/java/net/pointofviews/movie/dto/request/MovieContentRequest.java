package net.pointofviews.movie.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import net.pointofviews.movie.domain.MovieContentType;

public record MovieContentRequest(
        @NotNull(message = "영화 ID는 필수 입력 항목입니다.")
        Long movieId,

        @NotBlank(message = "url 입력")
        String content,

        @NotNull(message = "IMAGE or YOUTUBE")
        MovieContentType contentType
) {}
