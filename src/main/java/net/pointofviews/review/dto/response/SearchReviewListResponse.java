package net.pointofviews.review.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(description = "Response containing a list of reviews")
public record SearchReviewListResponse(

        @Schema(description = "검색한 영화 관련 리뷰 리스트")
        Page<SearchReviewResponse> reviews
) {
}
