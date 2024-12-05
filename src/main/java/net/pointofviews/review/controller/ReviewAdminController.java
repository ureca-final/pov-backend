package net.pointofviews.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.review.controller.specification.ReviewAdminSpecification;
import net.pointofviews.review.service.ReviewAdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class ReviewAdminController implements ReviewAdminSpecification {

    private final ReviewAdminService reviewAdminService;

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/{movieId}/reviews/{reviewId}/blind")
    public ResponseEntity<BaseResponse<Void>> blindReview(
        @AuthenticationPrincipal(expression = "member") Member loginMember,
        @PathVariable Long movieId,
        @PathVariable Long reviewId
    ) {
        reviewAdminService.blindReview(loginMember, movieId, reviewId);

        return BaseResponse.ok("리뷰가 성공적으로 숨김 처리 되었습니다.");
    }
}
