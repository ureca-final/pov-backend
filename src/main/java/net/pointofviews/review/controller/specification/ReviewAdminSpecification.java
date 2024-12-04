package net.pointofviews.review.controller.specification;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Review", description = "리뷰 관련 관리자 API")
public interface ReviewAdminSpecification {

	@Operation(
		summary = "리뷰 숨김",
		description = "관리자가 특정 영화에 대한 리뷰를 숨기는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "리뷰 숨김 성공",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "리뷰가 성공적으로 숨김 처리 되었습니다."
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "403",
			description = "리뷰 숨김 실패",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "접근 권한이 없습니다."
					}
					"""
				)
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "리뷰 숨김 실패",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "리뷰(Id: 1)는 존재하지 않습니다."
					}
					"""
				)
			)
		)
	})
	ResponseEntity<BaseResponse<Void>> blindReview(
		@AuthenticationPrincipal(expression = "member") Member loginMember,
		@Parameter(description = "영화 ID", example = "1") Long movieId,
		@Parameter(description = "리뷰 ID", example = "1") Long reviewId
	);
}
