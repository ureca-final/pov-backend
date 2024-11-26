package net.pointofviews.review.service.impl;

import org.springframework.stereotype.Service;

import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

	@Override
	public void saveReview(Long movieId, CreateReviewRequest request) {

	}

	@Override
	public ProofreadReviewResponse proofreadReview(Long movieId, ProofreadReviewRequest request) {
		return null;
	}

	@Override
	public void updateReview(Long movieId, Long reviewId, PutReviewRequest request) {

	}

	@Override
	public void deleteReview(Long movieId, Long reviewId) {

	}

	@Override
	public void blindReview(Long movieId, Long reviewId) {

	}

	@Override
	public ReadReviewListResponse findReviewByMovie(Long movieId) {
		return null;
	}

	@Override
	public ReadReviewListResponse findAllReview() {
		return null;
	}

	@Override
	public ReadReviewResponse findReviewDetail(Long reviewId) {
		return null;
	}

	@Override
	public void updateReviewLike(Long reviewId, Long likedId) {

	}
}
