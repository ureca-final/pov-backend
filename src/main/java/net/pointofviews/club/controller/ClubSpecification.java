package net.pointofviews.club.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.pointofviews.club.dto.response.ReadAllClubsListResponse;
import net.pointofviews.club.dto.response.ReadClubDetailsResponse;
import net.pointofviews.club.dto.response.ReadMyClubsListResponse;
import net.pointofviews.common.dto.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Club", description = "클럽 관련 API")
public interface ClubSpecification {

    @Operation(summary = "공개 그룹 전체 조회", description = "모든 공개 클럽 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<BaseResponse<ReadAllClubsListResponse>> readAllClubs();

    @Operation(summary = "클럽 상세 조회", description = "특정 클럽의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "조회 실패",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "존재하지 않는 클럽입니다."
                                    }
                                    """)
                    )
            )
    })
    ResponseEntity<BaseResponse<ReadClubDetailsResponse>> readClubDetails(
            @PathVariable String clubId
    );

    @Operation(summary = "내 그룹 조회", description = "사용자가 속한 모든 클럽 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공"
            )
    })
    ResponseEntity<BaseResponse<ReadMyClubsListResponse>> readMyClubs();
}
