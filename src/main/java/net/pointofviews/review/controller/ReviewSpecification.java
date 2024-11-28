package net.pointofviews.review.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import net.pointofviews.review.dto.response.ReadReviewResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Review", description = "리뷰 관련 API")
public interface ReviewSpecification {

	@Operation(
		summary = "리뷰 등록",
		description = "특정 영화에 대한 리뷰를 작성할 때 사용하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "201",
			description = "리뷰 등록 성공",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "리뷰가 성공적으로 등록되었습니다."
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "리뷰 등록 실패",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "영화(Id: 1)는 존재하지 않습니다."
					}
					"""
				)
			)
		)
	})
	ResponseEntity<BaseResponse<Void>> createReview(Long movieId, CreateReviewRequest request);

	@Operation(
		summary = "리뷰 교정",
		description = "AI 를 활용해 리뷰를 교정하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "리뷰 교정 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "리뷰 교정 실패",
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
	ResponseEntity<BaseResponse<ProofreadReviewResponse>> proofreadReview(Long movieId, ProofreadReviewRequest request);

	@Operation(
		summary = "리뷰 수정",
		description = "사용자가 작성한 리뷰를 수정하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "리뷰 수정 성공",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "리뷰가 성공적으로 수정되었습니다."
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "리뷰 수정 실패",
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
	ResponseEntity<BaseResponse<Void>> putReview(Long movieId, Long reviewId, PutReviewRequest request);

	@Operation(
		summary = "리뷰 삭제",
		description = "사용자가 작성한 리뷰를 삭제하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "리뷰 삭제 성공",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "리뷰가 성공적으로 삭제되었습니다."
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "리뷰 삭제 실패",
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
	ResponseEntity<BaseResponse<Void>> deleteReview(Long movieId, Long reviewId);

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

	@Operation(
		summary = "영화별 리뷰 조회",
		description = "특정 영화에 대한 모든 리뷰를 조회하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "영화별 리뷰 조회 성공"
		),
		@ApiResponse(
			responseCode = "404",
			description = "영화별 리뷰 조회 실패",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
						"message": "영화(Id: 1)는 존재하지 않습니다."
					}
					"""
				)
			)
		)
	})
	ResponseEntity<BaseResponse<ReadReviewListResponse>> readMovieReviews(
		@Parameter(description = "영화 ID", example = "1") Long movieId,
		@ParameterObject Pageable pageable
	);

	@Operation(
		summary = "리뷰 전체 조회",
		description = "모든 영화의 리뷰를 추천 순으로 조회하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "리뷰 전체 조회 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "리뷰 전체 조회 실패",
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
	ResponseEntity<BaseResponse<ReadReviewListResponse>> readReviews();

	@Operation(
		summary = "리뷰 상세 조회",
		description = "특정 영화의 리뷰를 상세 조회하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "리뷰 상세 조회 성공"
		),
		@ApiResponse(
			responseCode = "404",
			description = "리뷰 상세 조회 실패",
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
	ResponseEntity<BaseResponse<ReadReviewResponse>> readReviewDetail(@Parameter(description = "리뷰 ID", example = "1") Long reviewId);

	@Operation(
		summary = "리뷰 좋아요",
		description = "특정 영화의 리뷰를 `좋아요` 하는 API."
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "리뷰 좋아요 성공",
			content = @Content(
				mediaType = MediaType.APPLICATION_JSON_VALUE,
				examples = @ExampleObject(value = """
					{
					    "message": "리뷰 좋아요가 성공적으로 완료되었습니다."
					}
					""")
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "리뷰 좋아요 실패",
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
	ResponseEntity<BaseResponse<Void>> putReviewLike(Long movieId, Long reviewId);

}
