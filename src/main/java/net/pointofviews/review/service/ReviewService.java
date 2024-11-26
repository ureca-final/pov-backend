package net.pointofviews.review.service;

import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;

public interface ReviewService {

	void saveReview(Long movieId, CreateReviewRequest request);

	ProofreadReviewResponse proofreadReview(Long movieId, ProofreadReviewRequest request);

	void updateReview(Long movieId, Long reviewId, PutReviewRequest request);

	void deleteReview(Long movieId, Long reviewId);

	void blindReview(Long movieId, Long reviewId);

	ReadReviewListResponse findReviewByMovie(Long movieId);

	ReadReviewListResponse findAllReview();

	ReadReviewResponse findReviewDetail(Long reviewId);

	void updateReviewLike(Long reviewId, Long likedId);
}
