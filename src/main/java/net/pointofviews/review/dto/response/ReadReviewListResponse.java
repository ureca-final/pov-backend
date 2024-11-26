package net.pointofviews.review.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing a list of reviews")
public record ReadReviewListResponse(

	@Schema(description = "리뷰 리스트")
	List<ReadReviewResponse> reviews
) {
}
