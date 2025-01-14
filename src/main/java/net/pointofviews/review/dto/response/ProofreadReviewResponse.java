package net.pointofviews.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "리뷰 교정 응답 DTO")
public record ProofreadReviewResponse(

	@Schema(description = "원래 문장", example = "영화 정말 재밋고 감동적이였어요.")
	String originalText,

	@Schema(description = "교정한 문장", example = "영화 정말 재밌고 감동적이였어요.")
	String correctedText

) {
}
