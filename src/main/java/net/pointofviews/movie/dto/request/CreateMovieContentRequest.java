package net.pointofviews.movie.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화 컨텐츠 요청 DTO")
public record CreateMovieContentRequest(
        @Schema(description = "컨텐츠 URL", example = "https://example.com/content.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
        String contentUrl,

        @Schema(description = "컨텐츠 타입", example = "IMAGE", allowableValues = {"IMAGE", "YOUTUBE"}, requiredMode = Schema.RequiredMode.REQUIRED)
        String contentType
) {
}