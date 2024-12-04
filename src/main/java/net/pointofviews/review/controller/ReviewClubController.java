package net.pointofviews.review.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.review.controller.specification.ReviewClubSpecification;
import net.pointofviews.review.dto.response.ReadMyClubInfoListResponse;
import net.pointofviews.review.dto.response.ReadMyClubReviewsResponse;
import net.pointofviews.review.service.ReviewClubService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clubs")
public class ReviewClubController implements ReviewClubSpecification {

	private final ReviewClubService reviewClubService;

	@Override
	@GetMapping("/reviews")
	public ResponseEntity<BaseResponse<ReadMyClubInfoListResponse>> readMyClubsInfo(@AuthenticationPrincipal(expression = "member") Member loginMember) {
		ReadMyClubInfoListResponse response = reviewClubService.findMyClubList(loginMember);

		if (response.clubs().isEmpty()) {
			return BaseResponse.noContent();
		}

		return BaseResponse.ok("가입한 모든 클럽이 성공적으로 조회되었습니다.", response);
	}

	@Override
	@GetMapping("/{clubId}/reviews")
	public ResponseEntity<BaseResponse<ReadMyClubReviewsResponse>> readMyClubReviews(@PathVariable UUID clubId) {
		ReadMyClubReviewsResponse response = reviewClubService.findReviewByClub();

		return BaseResponse.ok("클럽별 리뷰가 성공적으로 조회되었습니다.", response);
	}
}
