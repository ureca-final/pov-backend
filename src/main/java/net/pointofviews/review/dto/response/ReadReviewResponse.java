package net.pointofviews.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "리뷰 DTO")
public record ReadReviewResponse(

	@Schema(description = "리뷰 ID", example = "1")
	Long reviewId,

	@Schema(description = "영화 ID", example = "1")
	Long movieId,

	@Schema(description = "영화 제목", example = "Inception")
	String movieTitle,

	@Schema(description = "리뷰 제목", example = "영화계의 걸작!")
	String title,

	@Schema(
		description = "리뷰 내용",
		example = "<p>이 영화는 정말 놀라웠습니다. 스토리 전개가 흥미진진하고 캐릭터들이 생동감 있게 느껴졌습니다.</p>"
			+ "<img src=\"https://example.com/images/movie_scene.jpg\" alt=\"영화 장면\" />"
	)
	String contents,

	@Schema(description = "작성자", example = "홍길동")
	String reviewer,

	@Schema(description = "작성자 프로필 이미지", example = "https://example.com/profile/profileImage.jpg")
	String profileImage,

	@Schema(description = "영화 포스터 URL", example = "https://example.com/posters/inception.jpg")
	String thumbnail,

	@Schema(description = "리뷰 작성 시간", example = "2024-11-26T10:30:00")
	LocalDateTime createdAt,

	@Schema(description = "리뷰 좋아요 수", example = "10")
	Long likeAmount,

	@Schema(description = "좋아요 여부", example = "true")
	boolean isLiked,

	@Schema(description = "스포일러 여부", example = "true")
	boolean spoiler

) {
}

