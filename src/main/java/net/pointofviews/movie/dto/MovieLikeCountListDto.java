package net.pointofviews.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "영화 좋아요 개수 리스트 DTO")
public record MovieLikeCountListDto(
        @Schema(description = "영화 좋아요 개수 데이터 리스트")
        List<MovieLikeCountDto> movieLikeCountList
) {
}
