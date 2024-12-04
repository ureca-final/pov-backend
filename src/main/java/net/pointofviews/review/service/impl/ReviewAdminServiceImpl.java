package net.pointofviews.review.service.impl;

import static net.pointofviews.member.exception.MemberException.*;
import static net.pointofviews.movie.exception.MovieException.*;
import static net.pointofviews.review.exception.ReviewException.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.ReviewAdminService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewAdminServiceImpl implements ReviewAdminService {

	private final ReviewRepository reviewRepository;
	private final MovieRepository movieRepository;
	private final MemberRepository memberRepository;

	@Override
	@Transactional
	public void blindReview(Member loginMember, Long movieId, Long reviewId) {

		if (memberRepository.findById(loginMember.getId()).isEmpty()) {
			throw adminNotFound(loginMember.getId());
		}

		if (movieRepository.findById(movieId).isEmpty()) {
			throw movieNotFound(movieId);
		}

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> reviewNotFound(reviewId));

		review.toggleDisabled();
	}
}

