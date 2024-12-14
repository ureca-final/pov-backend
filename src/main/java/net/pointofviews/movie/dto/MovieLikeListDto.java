package net.pointofviews.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "영화 좋아요 리스트 DTO")
public record MovieLikeListDto(

        @Schema(description = "영화 좋아요 데이터 리스트")
        List<MovieLikeDto> movieLikeList
) {
}
