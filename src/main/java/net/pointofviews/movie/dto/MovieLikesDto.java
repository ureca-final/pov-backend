package net.pointofviews.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "영화 좋아요 구현")
public record MovieLikesDto(
        Long movieId,
        Long memeberId,
        Boolean isLiked
) {
}
