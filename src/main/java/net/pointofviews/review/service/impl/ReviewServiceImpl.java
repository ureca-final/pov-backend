package net.pointofviews.review.service.impl;

import net.pointofviews.movie.domain.Movie;
import net.pointofviews.review.domain.ReviewKeywordLink;
import net.pointofviews.review.exception.ReviewNotFoundException;
import net.pointofviews.review.repository.ReviewKeywordLinkRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.pointofviews.movie.exception.MovieNotFoundException;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewLikeRepository;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final MovieRepository movieRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewLikeCountRepository reviewLikeCountRepository;
	private final ReviewKeywordLinkRepository reviewKeywordLinkRepository;

	@Override
	@Transactional
	public void saveReview(Long movieId, CreateReviewRequest request) {

		 Movie movie = movieRepository.findById(movieId)
            		.orElseThrow(MovieNotFoundException::new);

		// 리뷰 생성 및 저장
		Review review = Review.builder()
				.title(request.title())
				.contents(request.contents())
				.preference(request.preference())
				.isSpoiler(request.spoiler())
				.build();

		review.setMovie(movie);
		reviewRepository.save(review);

		// 키워드 저장
		if (request.keywords() != null && !request.keywords().isEmpty()) {
			for (String keyword : request.keywords()) {
				ReviewKeywordLink keywordLink = ReviewKeywordLink.builder()
						.review(review)
						.reviewKeywordCode(keyword)
						.build();

				reviewKeywordLinkRepository.save(keywordLink);
			}
		}
	}

	@Override
	public ProofreadReviewResponse proofreadReview(Long movieId, ProofreadReviewRequest request) {
		return null;
	}

	@Override
	@Transactional
	public void updateReview(Long movieId, Long reviewId, PutReviewRequest request) {
		if (movieRepository.findById(movieId).isEmpty()) {
			throw new MovieNotFoundException();
		}

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(ReviewNotFoundException::new);

		review.update(request.title(), request.contents());
	}

	@Override
	@Transactional
	public void deleteReview(Long movieId, Long reviewId) {
		if (movieRepository.findById(movieId).isEmpty()) {
			throw new MovieNotFoundException();
		}

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(ReviewNotFoundException::new);

		review.delete(); // soft delete 처리
	}

	@Override
	public void blindReview(Long movieId, Long reviewId) {

	}

	@Override
	public ReadReviewListResponse findReviewByMovie(Long movieId, Pageable pageable) {

		if (movieRepository.findById(movieId).isEmpty()) {
			throw new MovieNotFoundException();
		}

		Slice<Review> reviews = reviewRepository.findAllByMovieId(movieId, pageable);

		Slice<ReadReviewResponse> response = reviews.map(review -> {
			Long likeAmount = reviewLikeCountRepository.getReviewLikeCountByReviewId(review.getId());
			boolean isLiked = reviewLikeRepository.getIsLikedByReviewId(review.getId());

			return new ReadReviewResponse(
				review.getMovie().getTitle(),
				review.getTitle(),
				review.getContents(),
				review.getMember().getNickname(),
				review.getThumbnail(),
				review.getCreatedAt(),
				likeAmount,
				isLiked
			);
		});

		return new ReadReviewListResponse(response);
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
