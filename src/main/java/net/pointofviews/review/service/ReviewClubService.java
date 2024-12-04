package net.pointofviews.review.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.review.dto.response.ReadMyClubInfoListResponse;
import net.pointofviews.review.dto.response.ReadMyClubReviewsResponse;

public interface ReviewClubService {

	ReadMyClubInfoListResponse findMyClubList(Member loginMember);

	ReadMyClubReviewsResponse findReviewByClub();
}
