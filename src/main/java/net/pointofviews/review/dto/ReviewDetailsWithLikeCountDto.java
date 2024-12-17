package net.pointofviews.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import net.pointofviews.review.domain.ReviewPreference;

import java.time.LocalDateTime;

@Schema(description = "리뷰 상세 정보와 좋아요 수 DTO")
public record ReviewDetailsWithLikeCountDto(
        @Schema(description = "리뷰 ID", example = "1")
        Long id,

        @Schema(description = "리뷰 제목", example = "놀라운 영화!")
        String title,

        @Schema(description = "리뷰 내용", example = "이 영화는 놀라운 비주얼과 감동적인 스토리로 정말 최고였습니다.")
        String contents,

        @Schema(description = "썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
        String thumbnail,

        @Schema(description = "리뷰 선호도", example = "GOOD")
        ReviewPreference preference,

        @Schema(description = "스포일러 여부", example = "false")
        Boolean isSpoiler,

        @Schema(description = "리뷰 비활성화 여부", example = "false")
        Boolean disabled,

        @Schema(description = "리뷰 수정 일시", example = "2024-12-13T10:00:00")
        LocalDateTime modifiedAt,

        @Schema(description = "리뷰 좋아요 수", example = "25")
        Long likeCount,

        @Schema(description = "유저 프로필 이미지")
        String profileImage,

        @Schema(description = "유저 닉네임")
        String nickname
) {
}
