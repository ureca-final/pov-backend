package net.pointofviews.review.service.impl;

import lombok.RequiredArgsConstructor;
import net.pointofviews.member.domain.Member;
import net.pointofviews.member.repository.MemberRepository;
import net.pointofviews.movie.repository.MovieRepository;
import net.pointofviews.review.domain.Review;
import net.pointofviews.review.dto.response.SearchReviewListResponse;
import net.pointofviews.review.dto.response.SearchReviewResponse;
import net.pointofviews.review.repository.ReviewLikeCountRepository;
import net.pointofviews.review.repository.ReviewRepository;
import net.pointofviews.review.service.ReviewAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.pointofviews.member.exception.MemberException.adminNotFound;
import static net.pointofviews.movie.exception.MovieException.movieNotFound;
import static net.pointofviews.review.exception.ReviewException.reviewNotFound;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewAdminServiceImpl implements ReviewAdminService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final MemberRepository memberRepository;
    private final ReviewLikeCountRepository reviewLikeCountRepository;

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

    @Override
    public SearchReviewListResponse searchMovieReview(Member loginMember, String query, Pageable pageable) {
        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        Page<SearchReviewResponse> responses = reviewRepository.searchReviewByMovieTitle(query, pageable)
                .map(row -> new SearchReviewResponse(
                        ((Number) row[0]).longValue(),
                        ((Number) row[1]).longValue(),
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        (String) row[5],
                        (String) row[6],
                        (String) row[7],
                        ((java.sql.Timestamp) row[8]).toLocalDateTime(),
                        ((Number) row[9]).longValue(),
                        (Boolean) row[10]
                ));

        return new SearchReviewListResponse(responses);
    }

    @Override
    public SearchReviewResponse findReviewDetail(Member loginMember, Long reviewId) {

        if (memberRepository.findById(loginMember.getId()).isEmpty()) {
            throw adminNotFound(loginMember.getId());
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> reviewNotFound(reviewId));

        Long likeAmount = reviewLikeCountRepository.getReviewLikeCountByReviewId(reviewId).orElse(0L);

        SearchReviewResponse response = new SearchReviewResponse(
                reviewId,
                review.getMovie().getId(),
                review.getMovie().getTitle(),
                review.getTitle(),
                review.getContents(),
                review.getMember().getNickname(),
                review.getMember().getProfileImage(),
                review.getMovie().getPoster(),
                review.getCreatedAt(),
                likeAmount,
                review.isSpoiler()
        );

        return response;
    }
}

