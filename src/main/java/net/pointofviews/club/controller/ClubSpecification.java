package net.pointofviews.club.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import net.pointofviews.club.dto.request.*;
import net.pointofviews.club.dto.response.*;
import net.pointofviews.common.dto.BaseResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

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

    // 그룹 생성
    @Operation(summary = "클럽 생성", description = "💡새로운 클럽을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "⭕ CREATED"
            ),
            @ApiResponse(responseCode = "400", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "클럽 생성에 실패했습니다."
                                }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<CreateClubResponse>> createClub(@Valid @RequestBody CreateClubRequest request);

    // 그룹 수정
    @Operation(summary = "클럽 수정", description = "💡기존 클럽 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS"
            ),
            @ApiResponse(responseCode = "404", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "클럽 수정에 실패했습니다."
                                }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<PutClubResponse>> putClub(@PathVariable UUID clubId, @Valid @RequestBody PutClubRequest request);

    // 그룹 탈퇴
    @Operation(summary = "클럽 탈퇴", description = "💡클럽을 탈퇴합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "클럽 탈퇴가 완료되었습니다."
                                }""")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "클럽 탈퇴에 실패했습니다."
                                }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> deleteClub(@PathVariable UUID clubId);

    // 그룹장 변경
    @Operation(summary = "클럽장 변경", description = "💡클럽의 리더를 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS"),
            @ApiResponse(responseCode = "404", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                            {
                              "message": "클럽장 변경에 실패했습니다."
                            }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<PutClubLeaderResponse>> putClubLeader(
            @PathVariable UUID clubId,
            @Valid @RequestBody PutClubLeaderRequest request
    );

    // 그룹원 강퇴
    @Operation(summary = "클럽원 강퇴", description = "💡클럽에서 멤버를 강퇴합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "클럽원이 강퇴되었습니다."
                                }""")
                    )
            ),
            @ApiResponse(responseCode = "404", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "클럽원 강퇴에 실패했습니다."
                                }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<Void>> kickMemberFromClub(@PathVariable UUID clubId, @PathVariable UUID memberId);

    // 그룹원 목록 조회
    @Operation(summary = "클럽원 목록 조회", description = "💡클럽의 전체 멤버 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "⭕ SUCCESS"
            ),
            @ApiResponse(responseCode = "404", description = "❌ FAIL",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(value = """
                                {
                                  "message": "클럽원 목록 조회에 실패했습니다."
                                }""")
                    )
            )
    })
    ResponseEntity<BaseResponse<ReadClubMemberListResponse>> readClubMembers(@PathVariable UUID clubId);
}
