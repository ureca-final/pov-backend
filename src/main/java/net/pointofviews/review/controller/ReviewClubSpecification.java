package net.pointofviews.review.controller;

import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import net.pointofviews.member.domain.Member;
import net.pointofviews.review.dto.response.ReadMyClubInfoListResponse;
import net.pointofviews.review.dto.response.ReadMyClubReviewsResponse;
import net.pointofviews.common.dto.BaseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ClubReview", description = "클럽 리뷰 관련 API")
public interface ReviewClubSpecification {

	@Operation(
		summary = "가입한 클럽 조회",
		description = "사용자가 가입한 클럽을 조회하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "가입한 클럽 조회 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "가입한 클럽 조회 실패",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "잘못된 요청입니다."
					}
					"""
				)
			)
		)
	})
	ResponseEntity<BaseResponse<ReadMyClubInfoListResponse>> readMyClubsInfo(@AuthenticationPrincipal(expression = "member") Member loginMember);

	@Operation(
		summary = "클럽별 리뷰 조회",
		description = "사용자가 가입한 클럽별 모든 리뷰를 최신 순으로 조회하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "클럽별 리뷰 조회 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "클럽별 리뷰 조회 실패",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "잘못된 요청입니다."
					}
					"""
				)
			)
		)
	})
	ResponseEntity<BaseResponse<ReadMyClubReviewsResponse>> readMyClubReviews(@PathVariable UUID clubId);
}
