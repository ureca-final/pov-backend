package net.pointofviews.review.controller;

import net.pointofviews.review.dto.request.DeleteReviewImageListRequest;
import net.pointofviews.review.dto.response.CreateReviewImageListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.service.ReviewService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class ReviewController implements ReviewSpecification {

	private final ReviewService reviewService;

	@Override
	@PostMapping("/{movieId}/reviews")
	public ResponseEntity<BaseResponse<Void>> createReview(@PathVariable Long movieId, @Valid @RequestBody CreateReviewRequest request) {
		reviewService.saveReview(movieId, request);
		return BaseResponse.created("/movies/" + movieId + "/reviews", "리뷰가 성공적으로 등록되었습니다.");
	}

	@Override
	@PostMapping("/{movieId}/reviews/proofread")
	public ResponseEntity<BaseResponse<ProofreadReviewResponse>> proofreadReview(@PathVariable Long movieId, @Valid @RequestBody ProofreadReviewRequest request) {
		ProofreadReviewResponse response = reviewService.proofreadReview(movieId, request);

		return BaseResponse.ok("문장이 성공적으로 교정되었습니다.", response);
	}

	@Override
	@PutMapping("/{movieId}/reviews/{reviewId}")
	public ResponseEntity<BaseResponse<Void>> putReview(
		@PathVariable Long movieId,
		@PathVariable Long reviewId,
		@RequestBody PutReviewRequest request
	) {
		reviewService.updateReview(movieId, reviewId, request);
		return BaseResponse.ok("리뷰가 성공적으로 수정되었습니다.");
	}

	@Override
	@DeleteMapping("/{movieId}/reviews/{reviewId}")
	public ResponseEntity<BaseResponse<Void>> deleteReview(@PathVariable Long movieId, @PathVariable Long reviewId) {
		reviewService.deleteReview(movieId, reviewId);
		return BaseResponse.ok("리뷰가 성공적으로 삭제되었습니다.");
	}

	@Override
	@PutMapping("/{movieId}/reviews/{reviewId}/blind")
	public ResponseEntity<BaseResponse<Void>> blindReview(@PathVariable Long movieId, @PathVariable Long reviewId) {
		return BaseResponse.ok("리뷰가 성공적으로 숨김 처리 되었습니다.");
	}

	@Override
	@GetMapping("/{movieId}/reviews")
	public ResponseEntity<BaseResponse<ReadReviewListResponse>> readMovieReviews(
		@PathVariable Long movieId,
		@PageableDefault(page = 0, size = 10) Pageable pageable
	) {
		ReadReviewListResponse response = reviewService.findReviewByMovie(movieId, pageable);

		return BaseResponse.ok("영화별 리뷰가 성공적으로 조회되었습니다.", response);
	}

	@Override
	@GetMapping("/reviews")
	public ResponseEntity<BaseResponse<ReadReviewListResponse>> readReviews() {
		ReadReviewListResponse response = reviewService.findAllReview();

		return BaseResponse.ok("모든 리뷰가 성공적으로 조회되었습니다.", response);
	}

	@Override
	@GetMapping("/reviews/{reviewId}")
	public ResponseEntity<BaseResponse<ReadReviewResponse>> readReviewDetail(@PathVariable Long reviewId) {
		ReadReviewResponse response = reviewService.findReviewDetail(reviewId);

		return BaseResponse.ok("리뷰가 성공적으로 상세 조회되었습니다.", response);
	}

	@Override
	@PutMapping("/{movieId}/reviews/{reviewId}/likes")
	public ResponseEntity<BaseResponse<Void>> putReviewLike(@PathVariable Long movieId, @PathVariable Long reviewId) {
		return BaseResponse.ok("리뷰 좋아요가 성공적으로 완료되었습니다.");
	}

	@Override
	@PostMapping(value = "/reviews/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<BaseResponse<CreateReviewImageListResponse>> createReviewImages(
			@RequestPart(value = "files") List<MultipartFile> files
	) {
		CreateReviewImageListResponse response = reviewService.saveReviewImages(files);
		return BaseResponse.ok("이미지가 성공적으로 업로드되었습니다.", response);
	}

	@Override
	@DeleteMapping("/reviews/images")
	public ResponseEntity<BaseResponse<Void>> deleteReviewImages(@RequestBody DeleteReviewImageListRequest request) {
		reviewService.deleteReviewImages(request.imageUrls());
		return BaseResponse.ok("이미지가 성공적으로 삭제되었습니다.");
	}
}
