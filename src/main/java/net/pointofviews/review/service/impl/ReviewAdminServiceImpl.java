package net.pointofviews.review.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.common.service.S3Service;
import net.pointofviews.movie.domain.Movie;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.domain.ReviewKeywordLink;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.CreateReviewImageListResponse;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;
import net.pointofviews.review.exception.ImageException;
import net.pointofviews.review.repository.ReviewKeywordLinkRepository;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewLikeRepository;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.ReviewAdminService;
import net.pointofviews.review.service.ReviewMemberService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.pointofviews.movie.exception.MovieException.movieNotFound;
import static net.pointofviews.review.exception.ReviewException.reviewNotFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewAdminServiceImpl implements ReviewAdminService {

	private final ReviewRepository reviewRepository;
	private final MovieRepository movieRepository;
	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewLikeCountRepository reviewLikeCountRepository;
	private final ReviewKeywordLinkRepository reviewKeywordLinkRepository;
	private final S3Service s3Service;

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

