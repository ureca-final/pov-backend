package net.pointofviews.review.dto.response;

import java.util.UUID;

import org.springframework.data.domain.Slice;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "클럽별 리뷰 조회 DTO")
public record ReadMyClubReviewsResponse(

	@Schema(description = "클럽 ID", example = "a1s2d3f4@q7w8e9")
	UUID clubId,

	@Schema(description = "리뷰 리스트")
	Slice<ReadReviewResponse> reviews

) {
}
