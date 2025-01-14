package net.pointofviews.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(description = "리뷰 수정 요청 DTO")
public record PutReviewRequest(

	@Schema(description = "리뷰 제목", example = "수정 제목, 영화계의 걸작!")
	@NotBlank
	String title,

	@Schema(
		description = "리뷰 내용",
		example = "<p>수정 내용, 이 영화는 정말 놀라웠습니다. 스토리 전개가 흥미진진하고 캐릭터들이 생동감 있게 느껴졌습니다.</p>"
			+ "<img src=\"https://example.com/images/movie_scene.jpg\" alt=\"영화 장면\" />"
	)
	@NotBlank
	String contents,

	@Schema(description = "호감도", example = "부정적")
	@NotBlank
	String preference,

	@Schema(description = "키워드", example = "[\"기대 이하의\", \"지루한\"]")
	@NotNull
	List<String> keywords,

	@Schema(description = "스포일러 여부", example = "false")
	boolean spoiler
) {
}
