package net.pointofviews.review.controller.specification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.common.dto.BaseResponse;
import net.pointofviews.member.domain.Member;
import net.pointofviews.review.dto.response.ReadMyClubInfoListResponse;
import net.pointofviews.review.dto.response.ReadMyClubReviewListResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "ReviewClub-Member", description = "클럽 리뷰 관련 API")
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
                    responseCode = "204",
                    description = "아직 가입한 클럽 없음"
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
                    responseCode = "204",
                    description = "아직 클럽에 리뷰가 없음"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "클럽별 리뷰 조회 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                    	"message": "클럽(Id: a1s2f3@)이 존재하지 않습니다."
                                    }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<BaseResponse<ReadMyClubReviewListResponse>> readMyClubReviews(
            @PathVariable UUID clubId,
            @AuthenticationPrincipal(expression = "member") Member loginMember,
            @PageableDefault Pageable pageable
    );
}
