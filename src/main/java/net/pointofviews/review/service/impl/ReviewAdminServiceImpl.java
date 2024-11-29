package net.pointofviews.review.service.impl;

import static net.pointofviews.movie.exception.MovieException.*;
import static net.pointofviews.review.exception.ReviewException.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.ReviewAdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewAdminServiceImpl implements ReviewAdminService {

	private final ReviewRepository reviewRepository;
	private final MovieRepository movieRepository;

	@Override
	@Transactional
	public void blindReview(Long movieId, Long reviewId) {
		// TODO: 사용자 정보 가져오기

		if (movieRepository.findById(movieId).isEmpty()) {
			throw movieNotFound(movieId);
		}

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> reviewNotFound(reviewId));

		review.toggleDisabled();
	}
}

