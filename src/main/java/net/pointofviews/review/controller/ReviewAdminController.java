package net.pointofviews.review.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.review.service.ReviewAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class ReviewAdminController implements ReviewAdminSpecification{

    private final ReviewAdminService reviewAdminService;

    // TODO: 사용자 권한 확인
    @Override
    @PutMapping("/{movieId}/reviews/{reviewId}/blind")
    public ResponseEntity<BaseResponse<Void>> blindReview(@PathVariable Long movieId, @PathVariable Long reviewId) {
        reviewAdminService.blindReview(movieId, reviewId);
        return BaseResponse.ok("리뷰가 성공적으로 숨김 처리 되었습니다.");
    }
}
