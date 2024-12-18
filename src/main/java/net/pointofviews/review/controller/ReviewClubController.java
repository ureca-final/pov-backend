package net.pointofviews.review.controller;

import lombok.RequiredArgsConstructor;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.review.controller.specification.ReviewClubSpecification;
import net.pointofviews.review.dto.response.ReadMyClubInfoListResponse;
import net.pointofviews.review.dto.response.ReadMyClubReviewListResponse;
import net.pointofviews.review.service.ReviewClubService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/clubs")
public class ReviewClubController implements ReviewClubSpecification {

    private final ReviewClubService reviewClubService;

    @PreAuthorize("hasRole('ROLE_USER')")
    @Override
    @GetMapping("/reviews/my")
    public ResponseEntity<BaseResponse<ReadMyClubInfoListResponse>> readMyClubsInfo(@AuthenticationPrincipal(expression = "member") Member loginMember) {
        ReadMyClubInfoListResponse response = reviewClubService.findMyClubList(loginMember);

        if (response.clubs().isEmpty()) {
            return BaseResponse.noContent();
        }

        return BaseResponse.ok("가입한 모든 클럽이 성공적으로 조회되었습니다.", response);
    }

    @PreAuthorize("permitAll()")
    @Override
    @GetMapping("/{clubId}/reviews")
    public ResponseEntity<BaseResponse<ReadMyClubReviewListResponse>> readMyClubReviews(
            @PathVariable UUID clubId,
            @AuthenticationPrincipal MemberDetailsDto userDetail,
            @PageableDefault Pageable pageable
    ) {
        UUID memberId = userDetail != null ? userDetail.member().getId() : null;

        ReadMyClubReviewListResponse response = reviewClubService.findReviewByClub(clubId, memberId, pageable);

        if (response.reviews().isEmpty()) {
            return BaseResponse.noContent();
        }

        return BaseResponse.ok("클럽별 리뷰가 성공적으로 조회되었습니다.", response);
    }
}
