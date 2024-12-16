package net.pointofviews.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "영화 좋아요 DTO")
public record MovieLikeDto(
        @Schema(description = "영화 ID", example = "1")
        Long movieId,

        @Schema(description = "사용자 ID")
        UUID memeberId,

        @Schema(description = "좋아요 상태", example = "true")
        Boolean isLiked
) {
}
