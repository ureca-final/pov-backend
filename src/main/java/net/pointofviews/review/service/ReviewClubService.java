package net.pointofviews.review.service;

import net.pointofviews.member.domain.Member;
import net.pointofviews.review.dto.response.ReadMyClubInfoListResponse;
import net.pointofviews.review.dto.response.ReadMyClubReviewListResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewClubService {

    ReadMyClubInfoListResponse findMyClubList(Member loginMember);

    ReadMyClubReviewListResponse findReviewByClub(UUID clubId, Member loginMember, Pageable pageable);
}
