package net.pointofviews.review.service;

import net.pointofviews.member.domain.Member;

public interface ReviewAdminService {

	void blindReview(Member loginMember, Long movieId, Long reviewId);
}
