package net.pointofviews.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.review.domain.Review;

@Schema(description = "리뷰 상세 정보와 좋아요 수 DTO")
public record ReviewDetailsWithLikeCountDto(
        @Schema(description = "리뷰 정보",
                example = """
                {
                    "id": 1,
                    "title": "놀라운 영화!",
                    "contents": "이 영화는 놀라운 비주얼과 감동적인 스토리로 정말 최고였습니다.",
                    "thumbnail": "https://example.com/thumbnail.jpg",
                    "preference": "GOOD",
                    "isSpoiler": false,
                    "disabled": false,
                    "modifiedAt": "2024-12-13T10:00:00"
                }
                """)
        Review review,

        @Schema(description = "리뷰 좋아요 수", example = "25")
        Long likeCount
) {
}
