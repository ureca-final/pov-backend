package net.pointofviews.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

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
	ResponseEntity<BaseResponse<Void>> blindReview(Long movieId, Long reviewId);
}
