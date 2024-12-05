package net.pointofviews.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.review.controller.specification.ReviewMemberSpecification;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.CreateReviewImageListResponse;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewDetailResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.service.ReviewMemberService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class ReviewMemberController implements ReviewMemberSpecification {

    private final ReviewMemberService reviewMemberService;

    @Override
    @PostMapping("/{movieId}/reviews")
    public ResponseEntity<BaseResponse<Void>> createReview(@PathVariable Long movieId, @Valid @RequestBody CreateReviewRequest request, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        reviewMemberService.saveReview(movieId, request, memberDetailsDto.member());
        return BaseResponse.created("/movies/" + movieId + "/reviews", "리뷰가 성공적으로 등록되었습니다.");
    }

    @Override
    @PostMapping("/{movieId}/reviews/proofread")
    public ResponseEntity<BaseResponse<ProofreadReviewResponse>> proofreadReview(@PathVariable Long movieId, @Valid @RequestBody ProofreadReviewRequest request) {
        ProofreadReviewResponse response = reviewMemberService.proofreadReview(movieId, request);

        return BaseResponse.ok("문장이 성공적으로 교정되었습니다.", response);
    }

    @Override
    @PutMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<BaseResponse<Void>> putReview(
            @PathVariable Long movieId,
            @PathVariable Long reviewId,
            @RequestBody PutReviewRequest request,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        reviewMemberService.updateReview(movieId, reviewId, request, memberDetailsDto.member());
        return BaseResponse.ok("리뷰가 성공적으로 수정되었습니다.");
    }

    @Override
    @DeleteMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<BaseResponse<Void>> deleteReview(@PathVariable Long movieId, @PathVariable Long reviewId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        reviewMemberService.deleteReview(movieId, reviewId, memberDetailsDto.member());
        return BaseResponse.ok("리뷰가 성공적으로 삭제되었습니다.");
    }

    @Override
    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<BaseResponse<ReadReviewListResponse>> readMovieReviews(@PathVariable Long movieId, @PageableDefault Pageable pageable) {
        ReadReviewListResponse response = reviewMemberService.findReviewByMovie(movieId, pageable);

        return BaseResponse.ok("영화별 리뷰가 성공적으로 조회되었습니다.", response);
    }

    @Override
    @GetMapping("/reviews")
    public ResponseEntity<BaseResponse<ReadReviewListResponse>> readReviews() {
        ReadReviewListResponse response = reviewMemberService.findAllReview();

        return BaseResponse.ok("모든 리뷰가 성공적으로 조회되었습니다.", response);
    }

    @Override
    @GetMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<BaseResponse<ReadReviewDetailResponse>> readReviewDetail(@PathVariable Long movieId, @PathVariable Long reviewId) {
        ReadReviewDetailResponse response = reviewMemberService.findReviewDetail(reviewId);

        return BaseResponse.ok("리뷰가 성공적으로 상세 조회되었습니다.", response);
    }

    @Override
    @GetMapping("/reviews/my")
    public ResponseEntity<BaseResponse<ReadReviewListResponse>> readMyReviews(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PageableDefault Pageable pageable
    ) {
        ReadReviewListResponse response = reviewMemberService.findReviewByMember(loginMember, pageable);

        if (response.reviews().isEmpty()) {
            return BaseResponse.noContent();
        }

        return BaseResponse.ok("내 리뷰 조회가 성공적으로 조회되었습니다.", response);
    }

    @Override
    @PutMapping("/{movieId}/reviews/{reviewId}/likes")
    public ResponseEntity<BaseResponse<Void>> putReviewLike(@PathVariable Long movieId, @PathVariable Long reviewId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        reviewMemberService.updateReviewLike(movieId, reviewId, memberDetailsDto.member());
        return BaseResponse.ok("리뷰 좋아요가 성공적으로 완료되었습니다.");
    }

    @Override
    @PostMapping(value = "/{movieId}/reviews/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<CreateReviewImageListResponse>> createReviewImages(
            @PathVariable Long movieId,
            @RequestPart(value = "files") List<MultipartFile> files,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        CreateReviewImageListResponse response = reviewMemberService.saveReviewImages(files, movieId, memberDetailsDto.member());
        return BaseResponse.ok("이미지가 성공적으로 업로드되었습니다.", response);
    }

    @Override
    @DeleteMapping("/{movieId}/reviews/images")
    public ResponseEntity<BaseResponse<Void>> deleteReviewImagesFolder(
            @PathVariable Long movieId,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    ) {
        reviewMemberService.deleteReviewImagesFolder(movieId, memberDetailsDto.member());
        return BaseResponse.ok("이미지 폴더가 성공적으로 삭제되었습니다.");
    }
}
