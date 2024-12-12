package net.pointofviews.review.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.auth.dto.MemberDetailsDto;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.review.dto.request.CreateReviewRequest;
import net.pointofviews.review.dto.request.ProofreadReviewRequest;
import net.pointofviews.review.dto.request.PutReviewRequest;
import net.pointofviews.review.dto.response.CreateReviewImageListResponse;
import net.pointofviews.review.dto.response.ProofreadReviewResponse;
import net.pointofviews.review.dto.response.ReadReviewDetailResponse;
import net.pointofviews.review.dto.response.ReadReviewListResponse;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Review-Member", description = "리뷰 관련 사용자 API")
public interface ReviewMemberSpecification {

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
                    responseCode = "401",
                    description = "로그인이 필요한 서비스입니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                {
                    "message": "로그인이 필요한 서비스입니다."
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
    ResponseEntity<BaseResponse<Void>> createReview(Long movieId, CreateReviewRequest request, MemberDetailsDto memberDetailsDto);

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
                    responseCode = "401",
                    description = "로그인이 필요한 서비스입니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                {
                    "message": "로그인이 필요한 서비스입니다."
                }
                """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "리뷰 수정 권한이 없습니다.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                {
                    "message": "리뷰 수정 권한이 없습니다."
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
    ResponseEntity<BaseResponse<Void>> putReview(Long movieId, Long reviewId, PutReviewRequest request, MemberDetailsDto memberDetailsDto);

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
                    responseCode = "403",
                    description = "리뷰 삭제 권한 없음",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                    "message": "리뷰 삭제 권한이 없습니다."
                                }
                                """
                            )
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
    ResponseEntity<BaseResponse<Void>> deleteReview(Long movieId, Long reviewId, MemberDetailsDto memberDetailsDto);

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
    ResponseEntity<BaseResponse<ReadReviewListResponse>> readReviews(Pageable pageable);

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
    ResponseEntity<BaseResponse<ReadReviewDetailResponse>> readReviewDetail(
            @Parameter(description = "영화 ID", example = "1") Long movieId,
            @Parameter(description = "리뷰 ID", example = "1") Long reviewId
    );

    @Operation(
            summary = "내 리뷰 조회",
            description = "사용자가 작성한 모든 리뷰를 최신 순으로 조회하는 API."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "내 리뷰 조회"
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "아직 작성한 리뷰 없음"
            )
    })
    ResponseEntity<BaseResponse<ReadReviewListResponse>> readMyReviews(
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PageableDefault Pageable pageable
    );

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
    ResponseEntity<BaseResponse<Void>> putReviewLike(Long movieId, Long reviewId, MemberDetailsDto memberDetailsDto);

    @Operation(
            summary = "리뷰 이미지 업로드",
            description = "리뷰 작성 시 이미지를 업로드하는 API."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "이미지 업로드 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미지 업로드 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "지원하지 않는 파일 형식입니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "413",
                    description = "파일 크기 초과",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "파일 크기가 제한을 초과했습니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<CreateReviewImageListResponse>> createReviewImages(
            @PathVariable Long movieId,
            @RequestPart(value = "files") List<MultipartFile> files,
            @AuthenticationPrincipal MemberDetailsDto memberDetailsDto
    );

    @Operation(
            summary = "리뷰 이미지 삭제",
            description = "리뷰(등록한 리뷰, 임시저장된 리뷰 모두 포함)에 사용된 이미지를 삭제하는 API."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "이미지 삭제 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "이미지가 성공적으로 삭제되었습니다."
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미지 삭제 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "잘못된 이미지 URL입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> deleteReviewImagesFolder(Long movieId, MemberDetailsDto memberDetailsDto);
}
