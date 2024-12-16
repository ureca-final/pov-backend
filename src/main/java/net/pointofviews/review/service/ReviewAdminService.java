package net.pointofviews.review.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.review.dto.response.SearchReviewListResponse;
import org.springframework.data.domain.Pageable;

public interface ReviewAdminService {

    void blindReview(Member loginMember, Long movieId, Long reviewId);

    SearchReviewListResponse searchMovieReview(Member loginMember, String query, Pageable pageable);
}
