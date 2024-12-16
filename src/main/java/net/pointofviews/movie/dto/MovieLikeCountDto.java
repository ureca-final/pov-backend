package net.pointofviews.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화 좋아요 개수 DTO")
public record MovieLikeCountDto(
        @Schema(description = "영화 ID", example = "1")
        Long movieId,

        @Schema(description = "좋아요 개수", example = "500")
        Long likeCount
) {
}
