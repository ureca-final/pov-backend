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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_USER')")
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

    @PreAuthorize("permitAll()")
    @Override
    @GetMapping("/{movieId}/reviews")
    public ResponseEntity<BaseResponse<ReadReviewListResponse>> readMovieReviews(
            @AuthenticationPrincipal MemberDetailsDto memberDetail,
            @PathVariable Long movieId,
            @PageableDefault Pageable pageable
    ) {
        UUID memberId = memberDetail != null ? memberDetail.member().getId() : null;

        ReadReviewListResponse response = reviewMemberService.findReviewByMovie(memberId, movieId, pageable);

        return BaseResponse.ok("영화별 리뷰가 성공적으로 조회되었습니다.", response);
    }

    @Override
    @PreAuthorize("permitAll()")
    @GetMapping("/reviews")
    public ResponseEntity<BaseResponse<ReadReviewListResponse>> readReviews(@PageableDefault Pageable pageable,
                                                                            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {

        UUID memberId = Optional.ofNullable(memberDetailsDto)
                .map(MemberDetailsDto::member)
                .map(Member::getId)
                .orElse(null);

        ReadReviewListResponse response = reviewMemberService.findAllReview(pageable, memberId);

        return BaseResponse.ok("모든 리뷰가 성공적으로 조회되었습니다.", response);
    }

    @PreAuthorize("permitAll()")
    @Override
    @GetMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<BaseResponse<ReadReviewDetailResponse>> readReviewDetail(
            @AuthenticationPrincipal MemberDetailsDto memberDetail,
            @PathVariable Long movieId,
            @PathVariable Long reviewId
    ) {
        UUID memberId = memberDetail != null ? memberDetail.member().getId() : null;

        ReadReviewDetailResponse response = reviewMemberService.findReviewDetail(memberId, reviewId);

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
    @PostMapping("/{movieId}/reviews/{reviewId}/like")
    public ResponseEntity<BaseResponse<Void>> putReviewLike(@PathVariable Long movieId, @PathVariable Long reviewId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        reviewMemberService.updateReviewLike(movieId, reviewId, memberDetailsDto.member());
        return BaseResponse.ok("리뷰 좋아요가 성공적으로 완료되었습니다.");
    }

    @Override
    @PostMapping("/{movieId}/reviews/{reviewId}/dislike")
    public ResponseEntity<BaseResponse<Void>> putReviewDisLike(@PathVariable Long movieId, @PathVariable Long reviewId, @AuthenticationPrincipal MemberDetailsDto memberDetailsDto) {
        reviewMemberService.updateReviewDisLike(movieId, reviewId, memberDetailsDto.member());
        return BaseResponse.ok("리뷰 좋아요 취소가 성공적으로 완료되었습니다.");
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
