package net.pointofviews.review.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "리뷰 교정 요청 DTO")
public record ProofreadReviewRequest(

	@Schema(description = "교정할 문장", example = "영화 정말 재밋고 감동적이였어요.")
	@NotBlank
	String selectedText

) {
}
