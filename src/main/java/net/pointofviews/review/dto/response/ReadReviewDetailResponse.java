package net.pointofviews.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 상세 조회 DTO")
public record ReadReviewDetailResponse(

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

	@Schema(description = "영화 리뷰 포스터 URL", example = "https://example.com/thumbnails/inception.jpg")
	String thumbnail,

	@Schema(description = "리뷰 작성 시간", example = "2024-11-26T10:30:00")
	LocalDateTime createdAt,

	@Schema(description = "리뷰 좋아요 수", example = "10")
	Long likeAmount,

	@Schema(description = "좋아요 여부", example = "true")
	boolean isLiked,

	@Schema(description = "스포일러 여부", example = "true")
	boolean spoiler,

	@Schema(description = "키워드", example = "[\"흥미진진\", \"몰입감\"]")
	List<String> keywords

) {
}

