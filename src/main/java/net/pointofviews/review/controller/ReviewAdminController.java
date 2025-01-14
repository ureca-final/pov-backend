package net.pointofviews.review.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.review.controller.specification.ReviewAdminSpecification;
import net.pointofviews.review.dto.response.SearchReviewListResponse;
import net.pointofviews.review.dto.response.SearchReviewResponse;
import net.pointofviews.review.service.ReviewAdminService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/api/movies")
public class ReviewAdminController implements ReviewAdminSpecification {

    private final ReviewAdminService reviewAdminService;

    @Override
    @PutMapping("/{movieId}/reviews/{reviewId}/blind")
    public ResponseEntity<BaseResponse<Void>> blindReview(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long movieId,
            @PathVariable Long reviewId
    ) {
        reviewAdminService.blindReview(loginMember, movieId, reviewId);

        return BaseResponse.ok("리뷰가 성공적으로 숨김 처리 되었습니다.");
    }

    @Override
    @GetMapping("/reviews/search")
    public ResponseEntity<BaseResponse<SearchReviewListResponse>> searchReviews(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @RequestParam String query,
            @PageableDefault(size = 8) Pageable pageable
    ) {
        SearchReviewListResponse response = reviewAdminService.searchMovieReview(loginMember, query, pageable);

        if (response.reviews() == null) {
            return BaseResponse.noContent();
        }

        return BaseResponse.ok("검색한 영화와 관련된 모든 리뷰가 성공적으로 조회되었습니다.", response);
    }

    @Override
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<BaseResponse<SearchReviewResponse>> searchReviewDetail(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PathVariable Long reviewId
    ) {
        SearchReviewResponse response = reviewAdminService.findReviewDetail(loginMember, reviewId);

        return BaseResponse.ok("리뷰 상세 조회가 성공적으로 완료되었습니다.", response);
    }
}
